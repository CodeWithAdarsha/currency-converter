package com.currency.converter.adapter;

import com.currency.converter.exception.ResourceValidationException;
import com.currency.converter.model.CurrencyConversionBean;
import com.currency.converter.model.ExchangeRate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class CurrencyAdapter {
    @Autowired
    WebClient webClient;

    @Autowired
    ObjectMapper mapper;

    public Mono<String> loadCurrency(String from, String to, BigDecimal amount) {
        return webClient
                .get()
                .uri("daily/{code}.json", from)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new ResourceValidationException("Its 5xx Exception")))
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new ResourceValidationException("Its 4xx Exception")))
                .bodyToMono(String.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(5))
                                .jitter(0d)
                                .doAfterRetry(
                                        retrySignal -> {
                                            log.info("Retried {}", retrySignal.totalRetries());
                                        })
                                .filter(throwable -> throwable instanceof ResourceValidationException)
                                .onRetryExhaustedThrow(
                                        (retryBackoffSpec, retrySignal) ->
                                                new ResourceValidationException(
                                                        "Service failed to respond, after max attempts of: "
                                                                + retrySignal.totalRetries())))
                .doOnSuccess(
                        clientResponse -> {
                            log.info("Event is received by ");
                        })
                .doOnError(
                        ResourceValidationException.class,
                        (msg) -> {
                            log.error("Service failed to respond, after max attempts of retrySignal ");
                        });
    }


    @SneakyThrows
    public CurrencyConversionBean exchange(String from, String to, BigDecimal amount) {
        var exchangeData = loadCurrency(from, to, amount).block();
        var currencies = mapper.readValue(exchangeData, new TypeReference<Map<String, ExchangeRate>>() {
        });
        log.info("currency Data from {} to {}", from, currencies.get(to));
        var toCurrency = currencies.get(to);
        var exchangeRate = toCurrency.getRate();
        return new CurrencyConversionBean(from, to, exchangeRate, amount, amount.multiply(exchangeRate));
    }
}

