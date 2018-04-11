package com.el.robot.calculator.services.util;

import com.el.betting.sdk.v2.Currency;
import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.account.Money;
import com.el.robot.calculator.services.util.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExchangeRateConverter {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    public Stake sum(Stake initialStake, Stake... stakes) {
        Currency currency = initialStake.getCurrency();
        BigDecimal sumStake = initialStake.getAmount();
        for (Stake stake : stakes) {
            if (!stake.getCurrency().equals(currency)) {
                BigDecimal amount = stake.getAmount();
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(stake.getCurrency(), currency);
                sumStake = sumStake.add(amount.multiply(exchangeRate));
            } else {
                sumStake = sumStake.add(stake.getAmount());
            }
        }

        return new Stake(sumStake, currency);
    }

    public Stake convertToCurrency(Stake stake, Currency toCurrency) {
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(stake.getCurrency(), toCurrency);
        return new Stake(stake.getAmount().multiply(exchangeRate), toCurrency);
    }

    public Money convertToCurrency(Money money, Currency toCurrency) {
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(money.getCurrency(), toCurrency);
        return new Money(money.getAmount().multiply(exchangeRate), toCurrency);
    }
}
