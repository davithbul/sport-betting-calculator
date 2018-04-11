package com.el.robot.calculator.services.impl.outcome.layback;

import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroupShare;
import com.el.robot.calculator.services.AffiliateProfitCalculator;
import com.el.robot.calculator.services.TaxCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Distributes back, lay shares in a way, that if back wins the profit will be 0,
 * but if lay wins it will have all the possible profit.
 * If the game is not win-win loss will be shared equally.
 */
@Service
public class MaxLayOddsShareCalculator extends LayBackOddsShareCalculator {

    private final static int SCALE = 5;

    private LayBackOddsShareCalculator layBackOddsShareCalculator;

    @Autowired
    public MaxLayOddsShareCalculator(@Qualifier("equalLayBackShare") LayBackOddsShareCalculator layBackOddsShareCalculator, TaxCalculator taxCalculator, AffiliateProfitCalculator affiliateProfitCalculator) {
        super(taxCalculator, affiliateProfitCalculator);
        this.layBackOddsShareCalculator = layBackOddsShareCalculator;
    }

    @Override
    public LayBackBetOptionGroupShare calculateOddsShare(LayBackBetOptionGroup layBackBetOptionGroup) {
        //if not win-win use default share loss equally
        if(layBackBetOptionGroup.getBackBetOption().getPrice().compareTo(layBackBetOptionGroup.getLayBetOption().getPrice()) <= 0) {
            return layBackOddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);
        }

        LayBackBetOptionGroupShare betOptionGroupShare = new LayBackBetOptionGroupShare(layBackBetOptionGroup);

        //calculate back bet share
        BigDecimal backShare = BigDecimal.ONE.divide(layBackBetOptionGroup.getBackBetOption().getPrice(), SCALE, RoundingMode.HALF_UP);
        betOptionGroupShare.setBackBetShare(backShare);

        //calculate lay bet share
        BigDecimal layBetShare = BigDecimal.ONE.subtract(backShare);
        betOptionGroupShare.setLayBetShare(layBetShare);

        //calculate user bet share
        BigDecimal layUserBetShare = layBetShare.divide(layBackBetOptionGroup.getLayBetOption().getPrice().subtract(BigDecimal.ONE), SCALE, RoundingMode.HALF_UP);
        betOptionGroupShare.setLayUserBetShare(layUserBetShare);
        betOptionGroupShare.setLiabilityShare(layUserBetShare.multiply(layBackBetOptionGroup.getLayBetOption().getPrice()));

        return betOptionGroupShare;
    }
}
