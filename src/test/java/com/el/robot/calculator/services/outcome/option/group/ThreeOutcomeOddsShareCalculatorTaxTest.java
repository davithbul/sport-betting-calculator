package com.el.robot.calculator.services.outcome.option.group;

import com.el.betting.sdk.v2.OddsFormat;
import com.el.betting.sdk.v2.Period;
import com.el.betting.sdk.v2.Team;
import com.el.betting.sdk.v2.betoption.api.BetOption;
import com.el.betting.sdk.v2.betoption.bettype.moneyline.DefaultMoneyLineBetOption;
import com.el.betting.sdk.v2.pages.BettingPage;
import com.el.betting.sdk.v2.pages.WebBettingPage;
import com.el.betting.sdk.v2.provider.Bookmaker;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.services.OddsShareCalculator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class ThreeOutcomeOddsShareCalculatorTaxTest extends SpringJUnitTest {

    private final static double DELTA = 0.005;

    @Autowired
    private OddsShareCalculator oddsShareCalculator;

    private WebBettingPage withoutTaxWebBettingPage;
    private WebBettingPage fivePercentTaxWebBettingPage;

    @Before
    public void init() {
        withoutTaxWebBettingPage = new WebBettingPage(Bookmaker.ZERO_PERCENT, null, null);
        fivePercentTaxWebBettingPage = new WebBettingPage(Bookmaker.FIVE_PERCENT, null, null);
    }

    @Test
    public void testShareCalculationSplit() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), withoutTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);


        BetOptionGroupShare optionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);
        assertEquals(0.40, optionGroupShare.getBetShare(1).doubleValue(), DELTA);
        assertEquals(0.29, optionGroupShare.getBetShare(2).doubleValue(), DELTA);
        assertEquals(0.31, optionGroupShare.getBetShare(3).doubleValue(), DELTA);
    }

    @Test
    public void testShareCalculationSplitWithTax() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), fivePercentTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BetOptionGroupShare optionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);
        assertEquals(0.41444, optionGroupShare.getBetShare(1).doubleValue(), DELTA);
        assertEquals(0.28193, optionGroupShare.getBetShare(2).doubleValue(), DELTA);
        assertEquals(0.30362, optionGroupShare.getBetShare(3).doubleValue(), DELTA);
    }

    @Test
    public void testShareCalculation() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), withoutTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BetOptionGroupShare optionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        double homeTeamBetWin = optionGroupShare.getBetShare(1).multiply(optionGroupShare.getPrice(1)).doubleValue();
        double drawBetWin = optionGroupShare.getBetShare(2).multiply(optionGroupShare.getPrice(2)).doubleValue();
        double awayBetWin = optionGroupShare.getBetShare(3).multiply(optionGroupShare.getPrice(3)).doubleValue();
        assertEquals(homeTeamBetWin, drawBetWin, DELTA);
        assertEquals(homeTeamBetWin, awayBetWin, DELTA);
    }

    @Test
    public void testShareCalculationTax() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), withoutTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), fivePercentTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BetOptionGroupShare optionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        double homeTeamBetWin = optionGroupShare.getBetShare(1).multiply(optionGroupShare.getPrice(1)).doubleValue();
        double drawBetWin = optionGroupShare.getBetShare(2).multiply(optionGroupShare.getPrice(2)).multiply(BigDecimal.valueOf((100- fivePercentTaxWebBettingPage.getBookmaker().getTaxPercent()) / 100)).doubleValue();
        double awayBetWin = optionGroupShare.getBetShare(3).multiply(optionGroupShare.getPrice(3)).doubleValue();
        assertEquals(homeTeamBetWin, drawBetWin, DELTA);
        assertEquals(homeTeamBetWin, awayBetWin, DELTA);
    }

    @Test
    public void testShareCalculationSum() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), withoutTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BetOptionGroupShare optionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        double shareSum = optionGroupShare.getBetShare(1).add(optionGroupShare.getBetShare(2)).add(optionGroupShare.getBetShare(3)).doubleValue();
        assertEquals(1, shareSum, DELTA);
    }

    public BetOption createBetOption(BigDecimal price, BettingPage bettingPage) {
        return new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());

    }

}
