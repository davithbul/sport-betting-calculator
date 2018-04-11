package com.el.robot.calculator.services.impl.outcome.group;

import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.services.AffiliateProfitCalculator;
import com.el.robot.calculator.services.OddsCalculator;
import com.el.robot.calculator.services.TaxCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BetOptionGroupOddsCalculator implements OddsCalculator<BetOptionGroup> {

    @Autowired
    private BetOptionGroupShareCalculator oddsShareCalculator;

    @Autowired
    private TaxCalculator taxCalculator;

    @Autowired
    private AffiliateProfitCalculator affiliateProfitCalculator;

    @Override
    public boolean isWinWin(BetOptionGroup betOptionGroup) {
        BetOptionGroupShare betOptionShareGroup = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        //this should be also equal to awayTeamWin * awayTeamShare + homeTeamAffiliateReturn + drawAffiliateReturn
        int betOptionNumber = 1;
        BigDecimal oneSideWinGross = betOptionShareGroup.getPrice(betOptionNumber).multiply(betOptionShareGroup.getBetShare(betOptionNumber));
        BigDecimal oneSideWin = taxCalculator.applyTaxes(oneSideWinGross, betOptionShareGroup.getBettingPage(betOptionNumber).getBookmaker());

        //now add also all other affiliate returns
        BigDecimal allAffiliatesReturn = BigDecimal.ZERO;
        for(int i = 2; i <= betOptionShareGroup.getOptionCount(); i++) {
            BigDecimal affiliateReturn = betOptionShareGroup.getBetShare(i).multiply(affiliateProfitCalculator.getAffiliateReturn(betOptionGroup.getBettingPage(i)));
            allAffiliatesReturn = allAffiliatesReturn.add(affiliateReturn);
        }

        BigDecimal possibleWin = oneSideWin.add(allAffiliatesReturn);
        BigDecimal spent = BigDecimal.ONE;
        return possibleWin.compareTo(spent) == 1;
    }

    @Override
    public BigDecimal calculateWinPercentile(BetOptionGroup betOptionGroup) {
        BetOptionGroupShare betOptionShareGroup = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        //this should be also equal to awayTeamWin * awayTeamShare
        int betOptionNumber = 1;
        BigDecimal oneSideWinGross = betOptionShareGroup.getPrice(betOptionNumber).multiply(betOptionShareGroup.getBetShare(betOptionNumber));
        BigDecimal oneSideWin = taxCalculator.applyTaxes(oneSideWinGross, betOptionShareGroup.getBettingPage(betOptionNumber).getBookmaker());

        //now add also all other affiliate returns
        BigDecimal allAffiliatesReturn = BigDecimal.ZERO;
        for(int i = 2; i <= betOptionShareGroup.getOptionCount(); i++) {
            BigDecimal affiliateReturn = betOptionShareGroup.getBetShare(i).multiply(affiliateProfitCalculator.getAffiliateReturn(betOptionGroup.getBettingPage(i)));
            allAffiliatesReturn = allAffiliatesReturn.add(affiliateReturn);
        }

        BigDecimal possibleWin = oneSideWin.add(allAffiliatesReturn);
        BigDecimal spent = BigDecimal.ONE;

        BigDecimal difference = possibleWin.subtract(spent);
        return difference.multiply(BigDecimal.valueOf(100)).divide(spent, 2, RoundingMode.HALF_UP);
    }
}
