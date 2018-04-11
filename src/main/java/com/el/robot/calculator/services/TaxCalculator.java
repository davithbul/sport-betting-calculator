package com.el.robot.calculator.services;

import com.el.betting.sdk.v2.provider.Bookmaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TaxCalculator {

    public static BigDecimal calculateBeforeTaxAmount(BigDecimal amount, Bookmaker bookmaker) {
        return amount.multiply(BigDecimal.valueOf((100 + bookmaker.getTaxPercent()) / 100));
    }

    public BigDecimal applyTaxes(BigDecimal amount, Bookmaker bookmaker) {
        return amount.multiply(BigDecimal.valueOf((100 - bookmaker.getTaxPercent()) / 100));
    }

    public static double applyTaxes(double amount, Bookmaker bookmaker) {
        return amount * (100 - bookmaker.getTaxPercent()) / 100;
    }

    public BigDecimal calculateTax(BigDecimal amount, Bookmaker bookmaker) {
        return amount.multiply(BigDecimal.valueOf((bookmaker.getTaxPercent()) / 100));
    }

    public static boolean isOddHigher(Bookmaker bookmaker1, BigDecimal price1, Bookmaker bookmaker2, BigDecimal price2) {
        return isOddHigher(bookmaker1, price1.doubleValue(), bookmaker2, price2.doubleValue());
    }

    public static boolean isOddHigher(Bookmaker bookmaker1, double price1, Bookmaker bookmaker2, double price2) {
        if (bookmaker1 == null || price1 < 1) {
            return false;
        }

        if (bookmaker2 == null || price2 < 1) {
            return true;
        }

        return applyTaxes(price1, bookmaker1) > applyTaxes(price2, bookmaker2);
    }
}
