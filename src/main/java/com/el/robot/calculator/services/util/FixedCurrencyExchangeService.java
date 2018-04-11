package com.el.robot.calculator.services.util;

import com.el.betting.sdk.v2.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FixedCurrencyExchangeService implements CurrencyExchangeService {

    @Override
    public BigDecimal getExchangeRate(String baseCurrency, String toCurrency) {
        if("AUD".equalsIgnoreCase(baseCurrency) && toCurrency.equalsIgnoreCase("EUR")) {
            return BigDecimal.valueOf(0.70);
        } else if("EUR".equalsIgnoreCase(baseCurrency) && toCurrency.equalsIgnoreCase("AUD")) {
            return BigDecimal.valueOf(1.47);
        } else {
            throw new RuntimeException(String.format("Currency exchange hasn't been found %s -> %s", baseCurrency, toCurrency));
        }
    }

    @Override
    public BigDecimal getExchangeRate(Currency baseCurrency, Currency toCurrency) {
        if(baseCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        return getExchangeRate(baseCurrency.getCode(), toCurrency.getCode());
    }
}
