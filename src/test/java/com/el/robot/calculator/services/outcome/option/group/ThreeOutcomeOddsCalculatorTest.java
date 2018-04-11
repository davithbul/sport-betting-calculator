package com.el.robot.calculator.services.outcome.option.group;

import com.el.betting.sdk.v2.OddsFormat;
import com.el.betting.sdk.v2.Period;
import com.el.betting.sdk.v2.Team;
import com.el.betting.sdk.v2.betoption.api.BetOption;
import com.el.betting.sdk.v2.betoption.bettype.moneyline.DefaultMoneyLineBetOption;
import com.el.betting.sdk.v2.pages.WebBettingPage;
import com.el.betting.sdk.v2.provider.Bookmaker;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.services.impl.outcome.group.BetOptionGroupOddsCalculator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.*;


public class ThreeOutcomeOddsCalculatorTest extends SpringJUnitTest {

    private final static double DELTA = 0.004;

    @Autowired
    private BetOptionGroupOddsCalculator oddsCalculator;

    private WebBettingPage withoutTaxWebBettingPage;
    private WebBettingPage fivePercentTaxWebBettingPage;

    @Before
    public void init() {
        withoutTaxWebBettingPage = new WebBettingPage(Bookmaker.ZERO_PERCENT, null, null);
        fivePercentTaxWebBettingPage = new WebBettingPage(Bookmaker.FIVE_PERCENT, null, null);
    }

    @Test
    public void testWinWin() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25));

        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);
        boolean isWinWin = oddsCalculator.isWinWin(betOptionGroup);
        assertTrue(isWinWin);
    }

    @Test
    public void testWinWinAfterTax() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(3.1), fivePercentTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        assertEquals(7.19, percentile.doubleValue(), DELTA);
    }

    @Test
    public void testWinWinBothAfterTaxes() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(3.1), fivePercentTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), fivePercentTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25), fivePercentTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        assertEquals(3.71, percentile.doubleValue(), DELTA);
    }

    @Test
    public void testNotWinWin() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.3));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(2.8));
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        boolean isWinWin = oddsCalculator.isWinWin(betOptionGroup);
        assertFalse(isWinWin);
    }


    @Test
    public void testNotWinWinGameOdds() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.1));
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        boolean isWinWin = oddsCalculator.isWinWin(betOptionGroup);
        assertFalse(isWinWin);
    }

    @Test
    public void testWinWinPercentile() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.4));
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        assertEquals(2.06, percentile.doubleValue(), DELTA);
    }


    @Test
    public void testNotWinWinPercentileAfterTaxes() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5), fivePercentTaxWebBettingPage);
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5), withoutTaxWebBettingPage);
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.4), withoutTaxWebBettingPage);
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        assertTrue(percentile.doubleValue() < 0);
    }


    @Test
    public void testNotWinWinPercentile() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.1));
        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2, betOption3);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        assertTrue(percentile.doubleValue() < 0);
    }

    public BetOption createBetOption(BigDecimal price) {
        WebBettingPage bettingPage = new WebBettingPage(Bookmaker.Bet365, null, null);
        return createBetOption(price, bettingPage);
    }

    public BetOption createBetOption(BigDecimal price, WebBettingPage bettingPage) {
        return new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());
    }
}
