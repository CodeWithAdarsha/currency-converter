package com.currency.converter;

import com.currency.converter.adapter.CurrencyAdapter;
import com.currency.converter.model.CurrencyConversionBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyConversionControllerTest.class)
@ComponentScan(basePackages = "com.currency.converter")
public class CurrencyConversionControllerTest {
    String basePath = "/v1/api/currency/convert";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CurrencyAdapter currencyAdapter;

    @Test
    @DisplayName("currency converter success Test")
    public void currencyConverterSucessTest() throws Exception {

        String fakeResponse = "{\"base\":\"USD\",\"target\":\"INR\",\"exchangeRate\":81.138889058161,\"amount\":100,\"convertedAmount\":81.38889058161}";

        BigDecimal exchangeRate = BigDecimal.valueOf(81.138889058161);
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal convertedAmount = BigDecimal.valueOf(81.38889058161);

        CurrencyConversionBean mockCurrencyBean = new CurrencyConversionBean(
                "USD", "INR", exchangeRate, amount, convertedAmount);

        when(currencyAdapter.exchange(anyString(), anyString(), any())).thenReturn(mockCurrencyBean);

        // perform request and assert response
        var responseBody = mockMvc.perform(get(basePath)
                        .param("from", "USD")
                        .param("to", "INR")
                        .param("amount", "100"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        assertEquals(fakeResponse, responseBody);

        verify(currencyAdapter, times(1)).exchange(anyString(), anyString(), any());
        verifyNoMoreInteractions(currencyAdapter);
    }

    @Test
    @DisplayName("currency converter bad request Test")
    public void currencyConverterBadRequestTest() throws Exception {

        when(currencyAdapter.exchange("null", "INR", BigDecimal.valueOf(100))).thenThrow(new IllegalArgumentException("Invalid currency code"));
        mockMvc.perform(get(basePath)
                        .param("to", "INR")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest());
    }
}

