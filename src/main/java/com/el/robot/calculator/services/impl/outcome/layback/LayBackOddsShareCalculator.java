package com.el.robot.calculator.services.impl.outcome.layback;

import com.el.betting.sdk.v2.pages.BettingPage;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroupShare;
import com.el.robot.calculator.services.AffiliateProfitCalculator;
import com.el.robot.calculator.services.OddsShareCalculator;
import com.el.robot.calculator.services.TaxCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service("equalLayBackShare")
public class LayBackOddsShareCalculator implements OddsShareCalculator<LayBackBetOptionGroup, LayBackBetOptionGroupShare> {

    private final static int SCALE = 5;

    private TaxCalculator taxCalculator;
    private AffiliateProfitCalculator affiliateProfitCalculator;

    @Autowired
    public LayBackOddsShareCalculator(TaxCalculator taxCalculator, AffiliateProfitCalculator affiliateProfitCalculator) {
        this.taxCalculator = taxCalculator;
        this.affiliateProfitCalculator = affiliateProfitCalculator;
    }

    @Override
    public LayBackBetOptionGroupShare calculateOddsShare(LayBackBetOptionGroup layBackBetOptionGroup) {
        BettingPage bettingPage = layBackBetOptionGroup.getLayBetOption().getBettingPage();
        BigDecimal backStake =
                taxCalculator.applyTaxes(BigDecimal.ONE, bettingPage.getBookmaker()) //tax percent
                        .divide(layBackBetOptionGroup.getLayBetOption().getPrice().subtract(BigDecimal.ONE), SCALE, RoundingMode.HALF_UP)
                        .add(BigDecimal.ONE);

        BigDecimal layStake =
                taxCalculator.applyTaxes(layBackBetOptionGroup.getBackBetOption().getPrice(), layBackBetOptionGroup.getBackBetOption().getBettingPage().getBookmaker())
                        .subtract(affiliateProfitCalculator.getAffiliateReturn(layBackBetOptionGroup.getBackBetOption().getBettingPage()));

        BigDecimal sumStake = backStake.add(layStake);

        LayBackBetOptionGroupShare betOptionGroupShare = new LayBackBetOptionGroupShare(layBackBetOptionGroup);

        //calculate back bet share
        BigDecimal backShare = backStake.divide(sumStake, SCALE, RoundingMode.HALF_UP);
        betOptionGroupShare.setBackBetShare(backShare);

        //calculate lay bet share
        BigDecimal layBetShare = layStake.divide(sumStake, SCALE, RoundingMode.HALF_UP);
        betOptionGroupShare.setLayBetShare(layBetShare);

        //calculate user bet share
        BigDecimal layUserBetShare = layBetShare.divide(layBackBetOptionGroup.getLayBetOption().getPrice().subtract(BigDecimal.ONE), SCALE, RoundingMode.HALF_UP);
        betOptionGroupShare.setLayUserBetShare(layUserBetShare);
        betOptionGroupShare.setLiabilityShare(layUserBetShare.multiply(layBackBetOptionGroup.getLayBetOption().getPrice()));

        return betOptionGroupShare;
    }
}
