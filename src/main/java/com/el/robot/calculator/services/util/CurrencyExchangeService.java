package com.el.robot.calculator.services.util;

import com.el.betting.sdk.v2.Currency;

import java.math.BigDecimal;

public interface CurrencyExchangeService {

    BigDecimal getExchangeRate(String baseCurrency, String toCurrency);

    BigDecimal getExchangeRate(Currency baseCurrency, Currency toCurrency);
}
