package com.el.robot.calculator.services.impl.outcome.group;

import com.el.betting.sdk.v2.betoption.api.BetOption;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.services.AffiliateProfitCalculator;
import com.el.robot.calculator.services.OddsShareCalculator;
import com.el.robot.calculator.services.TaxCalculator;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Service
public class BetOptionGroupShareCalculator implements OddsShareCalculator<BetOptionGroup, BetOptionGroupShare> {

    private final static int SCALE = 5;

    @Autowired
    private TaxCalculator taxCalculator;

    @Autowired
    private AffiliateProfitCalculator affiliateProfitCalculator;

    /**
     * Unlike Equal odds share calculator, here we are not calculating
     * affiliate loss, but we calculate only affiliate gain.
     * if Stake1 wins then possible win amount will be:
     * Stake1 X Odd1 X Tax1 + Stake2 X Aff2 + Stake3 X Aff3 + ... + StakeN X AffN
     * StakeN X OddN X TaxN - StakeN X AffN = StakeK X oddK X TaxK - StakeK X AffK
     */
    public BetOptionGroupShare calculateOddsShare(BetOptionGroup betOptionGroup) {
        Preconditions.checkArgument(betOptionGroup.getOptionCount() >= 2);
        //Stake1 X Odd1 X (1 - Tax1) - Stake1 X Affiliate1 = Stake2 X Odd2 X (1 - Tax2) - Stake2 X Affiliate2
        //stake2 = (Stake1 X Odd1 X (1 - Tax1) - Stake1 X Affiliate1)/(Odd2 X (1 - Tax2) - Affiliate2)
        //(Stake1 X Odd1 X (1 - Tax1) - Stake1 X Affiliate1) => oneSideProfit
        BigDecimal firstStake = new BigDecimal("10").pow(Math.min(betOptionGroup.getOptionCount(), 10));

        BigDecimal winAmount = taxCalculator.applyTaxes(firstStake.multiply(betOptionGroup.getPrice(1)), betOptionGroup.getBettingPage(1).getBookmaker());
        BigDecimal affiliateLoss = affiliateProfitCalculator.calculateAffiliateReturn(firstStake, betOptionGroup.getBettingPage(1));
        BigDecimal oneSideProfit = winAmount.subtract(affiliateLoss);

        //calculate each stake and put in stakes array
        BigDecimal[] stakes = new BigDecimal[betOptionGroup.getOptionCount()];
        for (int i = 1; i <= betOptionGroup.getOptionCount(); i++) {
            //factor = odd X (1 - tax) - affiliate
            BigDecimal factor = taxCalculator.applyTaxes(betOptionGroup.getPrice(i), betOptionGroup.getBettingPage(i).getBookmaker())
                    .subtract(affiliateProfitCalculator.getAffiliateReturn(betOptionGroup.getBettingPage(i)));

            //stakeN = (oneSideProfit) / TaxN - AffiliateN
            BigDecimal stake = oneSideProfit.divide(factor, SCALE, RoundingMode.HALF_UP);
            stakes[i - 1] = stake;
        }

        //sum of all stakes
        BigDecimal sumStake = Arrays.stream(stakes).reduce(BigDecimal.ZERO, BigDecimal::add);

        BetOptionGroupShare betOptionShareGroup = new BetOptionGroupShare(betOptionGroup);
        for (int i = 1; i <= betOptionGroup.getOptionCount(); i++) {
            BigDecimal stake = stakes[i - 1];
            BigDecimal share = stake.divide(sumStake, SCALE, RoundingMode.HALF_UP);
            BetOption betOption = betOptionGroup.getBetOption(i);
            betOptionShareGroup.setBetOptionShare(i, betOption, share);
        }

        return betOptionShareGroup;
    }
}
