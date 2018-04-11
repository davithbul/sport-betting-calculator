package com.el.robot.calculator.services.impl.outcome.layback;

import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroupShare;
import com.el.robot.calculator.services.AffiliateProfitCalculator;
import com.el.robot.calculator.services.OddsCalculator;
import com.el.robot.calculator.services.TaxCalculator;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LayBackOddsCalculator implements OddsCalculator<LayBackBetOptionGroup> {

    private LayBackOddsShareCalculator oddsShareCalculator;

    private AffiliateProfitCalculator affiliateProfitCalculator;

    private TaxCalculator taxCalculator;


    @Autowired
    public LayBackOddsCalculator(@Qualifier("equalLayBackShare") LayBackOddsShareCalculator oddsShareCalculator, AffiliateProfitCalculator affiliateProfitCalculator, TaxCalculator taxCalculator) {
        this.oddsShareCalculator = oddsShareCalculator;
        this.affiliateProfitCalculator = affiliateProfitCalculator;
        this.taxCalculator = taxCalculator;
    }

    @Override
    public boolean isWinWin(LayBackBetOptionGroup betOptionGroup) {
        return calculateWinPercentile(betOptionGroup).compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public BigDecimal calculateWinPercentile(LayBackBetOptionGroup betOptionGroup) {
        LayBackBetOptionGroupShare betOptionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        //calculate possible wins
        BigDecimal backBetWin = taxCalculator.applyTaxes(betOptionGroupShare.getBackBetShare().multiply(betOptionGroup.getBackBetOption().getPrice()), betOptionGroup.getBackBetOption().getBettingPage().getBookmaker());

        BigDecimal layStraightWin = taxCalculator.applyTaxes(betOptionGroupShare.getLayUserBetShare(), betOptionGroupShare.getLayBetOption().getBettingPage().getBookmaker())
                .add(betOptionGroupShare.getLayBetShare());

        BigDecimal layBetWin = layStraightWin.add(betOptionGroupShare.getBackBetShare().multiply(affiliateProfitCalculator.getAffiliateReturn(betOptionGroupShare.getBackBetOption().getBettingPage())));
        Preconditions.checkArgument(Math.abs(backBetWin.subtract(layBetWin).floatValue()) < 0.004);

        BigDecimal spent = BigDecimal.ONE;
        BigDecimal difference = backBetWin.subtract(spent);
        return difference.multiply(BigDecimal.valueOf(100)).divide(spent, 2, RoundingMode.HALF_UP);
    }
}
