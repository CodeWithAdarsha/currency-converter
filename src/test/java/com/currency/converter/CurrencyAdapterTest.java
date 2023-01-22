package com.currency.converter;

import com.currency.converter.adapter.CurrencyAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CurrencyAdapterTest {

    @InjectMocks
    private CurrencyAdapter currencyAdapter;
    @Mock
    private WebClient webClientMock;

    @Mock
    private WebTestClient.Builder builder;

    @Test
    public void testLoadCurrency() {
        String mockResponse = """
                {
                   "usd": {
                     "code": "USD",
                     "alphaCode": "USD",
                     "numericCode": "840",
                     "name": "U.S. Dollar",
                     "rate": 1.0827620909104,
                     "date": "Fri, 20 Jan 2023 23:55:02 GMT",
                     "inverseRate": 0.92356391897612
                   },
                   "gbp": {
                     "code": "GBP",
                     "alphaCode": "GBP",
                     "numericCode": "826",
                     "name": "U.K. Pound Sterling",
                     "rate": 0.87611253732102,
                     "date": "Fri, 20 Jan 2023 23:55:02 GMT",
                     "inverseRate": 1.1414058781282
                   }
                 }""";


        var exchangeData = "{\"USD_INR\":{\"rate\":75.0}}";
        when(webClientMock.get().uri("daily/{code}.json", "USD").accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block()).thenReturn(exchangeData);


        var outlogMono = currencyAdapter.loadCurrency("usd", "inr", BigDecimal.valueOf(1));

    }

}
