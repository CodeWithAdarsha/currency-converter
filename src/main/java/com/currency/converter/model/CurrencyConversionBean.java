package com.currency.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionBean {

    private String base;
    private String target;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

}
