package com.el.robot.calculator.services;

import com.el.betting.sdk.v2.pages.AffiliateBettingPage;
import com.el.betting.sdk.v2.pages.BettingPage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class AffiliateProfitCalculator {

    public BigDecimal getAffiliateReturn(BettingPage bettingPage) {
        if (bettingPage instanceof AffiliateBettingPage) {
            return BigDecimal.valueOf(((AffiliateBettingPage) bettingPage).getAffiliateStrategy().getReturnPercentage() / 100);
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal calculateAffiliateReturn(BigDecimal amount, BettingPage bettingPage) {
        BigDecimal affiliateReturnPercent = getAffiliateReturn(bettingPage);
        return affiliateReturnPercent.multiply(amount,  new MathContext(2, RoundingMode.HALF_UP));
    }
}
