package com.currency.converter.controller;

import com.currency.converter.adapter.CurrencyAdapter;
import com.currency.converter.model.CurrencyConversionBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/v1/api/currency/")
@Tag(name = "Currency Converter", description = "Currency Conversion Controller")
public class CurrencyConversionController {
    @Autowired
    CurrencyAdapter currencyAdapter;

    @Operation(
            summary = " Currency Data API ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "successful operation"),
            })
    @GetMapping("convert")
    CurrencyConversionBean getCurrencyConverter(
            @Parameter(name = "from", in = ParameterIn.QUERY, description = "The three-letter currency code of the currency you would like to convert from.", required = true)
            @RequestParam(value = "from") String from,

            @Parameter(name = "to", in = ParameterIn.QUERY, description = "The three-letter currency code of the currency you would like to convert to.", required = true)
            @RequestParam(value = "to") String to,

            @Parameter(name = "amount", in = ParameterIn.QUERY, description = "The amount to be converted", required = true)
            @RequestParam(value = "amount") BigDecimal amount) {
        return currencyAdapter.exchange(from, to, amount);
    }


}
