package com.currency.converter.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClientFloatRates(@Value("${base-url}") String baseURL) {
        final var httpClient =
                HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new ReadTimeoutHandler(2000)).addHandlerLast(new WriteTimeoutHandler(2000)));

        return WebClient.builder()
                .baseUrl(baseURL)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("--- Http Headers: ---");
            clientRequest.headers().forEach(this::logHeader);
            log.info("--- Http Cookies: ---");
            clientRequest.cookies().forEach(this::logHeader);
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    log.info("Response: {}", clientResponse.statusCode());
                    clientResponse
                            .headers()
                            .asHttpHeaders()
                            .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
                    return Mono.just(clientResponse);
                });
    }

    private void logHeader(String name, List<String> values) {
        values.forEach(value -> log.info("{} --- {}", name, value));
    }
}
