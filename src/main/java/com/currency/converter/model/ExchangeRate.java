package com.currency.converter.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRate {
    private String code;
    private String alphaCode;
    private String numericCode;
    private String name;
    private BigDecimal rate;
    private String date;
    private BigDecimal inverseRate;
}
