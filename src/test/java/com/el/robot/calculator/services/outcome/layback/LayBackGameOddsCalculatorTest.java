package com.el.robot.calculator.services.outcome.layback;

import com.el.betting.sdk.v2.*;import com.el.betting.sdk.v4.*;
import com.el.betting.sdk.v2.betoption.bettype.layback.LayBackBetOption;
import com.el.betting.sdk.v2.betoption.bettype.moneyline.DefaultMoneyLineBetOption;
import com.el.betting.sdk.v2.pages.AffiliateBettingPage;
import com.el.betting.sdk.v2.pages.ApiBettingPage;
import com.el.betting.sdk.v2.pages.BettingPage;
import com.el.betting.sdk.v2.pages.WebBettingPage;
import com.el.betting.sdk.v2.provider.Bookmaker;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroupShare;
import com.el.robot.calculator.services.impl.outcome.layback.LayBackOddsCalculator;
import com.el.robot.calculator.services.impl.outcome.layback.LayBackOddsShareCalculator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static org.junit.Assert.*;


public class LayBackGameOddsCalculatorTest extends SpringJUnitTest {

    private final static double DELTA = 0.004;

    private WebBettingPage withoutTaxWebBettingPage;
    private WebBettingPage fivePercentTaxWebBettingPage;

    @Before
    public void init() {
        withoutTaxWebBettingPage = new WebBettingPage(Bookmaker.ZERO_PERCENT);
        fivePercentTaxWebBettingPage = new WebBettingPage(Bookmaker.FIVE_PERCENT_AFFILIATE);
    }

    @Autowired
    private LayBackOddsCalculator oddsCalculator;

    @Autowired
    @Qualifier("equalLayBackShare")
    private LayBackOddsShareCalculator oddsShareCalculator;

    @Test
    public void testWinWin() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);
        BigDecimal bigDecimal = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertEquals(0.0, bigDecimal.doubleValue(), DELTA);
    }

    @Test
    public void testHigherLayBack() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.BACK, new AffiliateBettingPage(Bookmaker.SkyBet));
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2.1), BetExchangeType.LAY, new ApiBettingPage(Bookmaker.Matchbook));
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal bigDecimal = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        System.out.println(bigDecimal);

        assertEquals(4.11, bigDecimal.doubleValue(), DELTA);
    }

    @Test
    public void testNotWinWin() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(1.9), BetExchangeType.BACK, fivePercentTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2.1), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        boolean isWinWin = oddsCalculator.isWinWin(layBackBetOptionGroup);
        assertFalse(isWinWin);
    }

    @Test
    public void testWinWinPercentile() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(5), BetExchangeType.BACK, fivePercentTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(5), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertEquals(-1.04, percentile.doubleValue(), DELTA);
    }


    @Test
    public void testNotWinWinPercentileAfterTaxes() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2.1), BetExchangeType.LAY, fivePercentTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertTrue(percentile.doubleValue() < 0);
    }

    @Test
    public void testNotWinWinPercentile() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2.5), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(1.6), BetExchangeType.LAY, fivePercentTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertEquals(percentile.doubleValue(), 27.05, DELTA);
    }

    @Test
    public void testWinWinPercentileBothTaxable() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2.5), BetExchangeType.BACK, fivePercentTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2.3), BetExchangeType.LAY, fivePercentTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertEquals(percentile.doubleValue(), 0.12, DELTA);
    }

    @Test
    public void testWithoutAffiliateRealTest() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(5), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(3.75), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        LayBackBetOptionGroupShare groupShare = oddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);
        BigDecimal backReturn = groupShare.getBackBetOption().getPrice().multiply(groupShare.getBackBetShare());
        BigDecimal layReturn = groupShare.getLayBetOption().getPrice().multiply(groupShare.getLayUserBetShare());
        assertEquals(backReturn.doubleValue(), layReturn.doubleValue(), DELTA);

        BigDecimal backStake = BigDecimal.valueOf(20).divide(groupShare.getLayUserBetShare(), 2, RoundingMode.HALF_UP)
                .multiply(groupShare.getBackBetShare());
        if (backStake.doubleValue() < 5) {
            backStake = BigDecimal.valueOf(5);
        }
        Stake newStake = new Stake(backStake, Currency.of("AUD"));
        System.out.println(newStake);
    }

    @Test
    public void otherTest() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(4), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<LayBackBetOption>(backBetOption, layBetOption);
        LayBackBetOptionGroupShare groupShare = oddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);

        BigDecimal backStake = BigDecimal.valueOf(10);
        BigDecimal layStake = backStake.divide(groupShare.getBackBetShare(), 4, RoundingMode.HALF_UP)
                .multiply(groupShare.getLayUserBetShare());
        System.out.println(layStake.subtract(backStake));
    }

    @Test
    public void testWinWinPercentileBothTaxableAffiliate() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2.5), BetExchangeType.BACK, new AffiliateBettingPage(Bookmaker.ZERO_PERCENT_AFFILIATE_25));
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2.3), BetExchangeType.LAY, fivePercentTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        BigDecimal percentile = oddsCalculator.calculateWinPercentile(layBackBetOptionGroup);
        assertEquals(percentile.doubleValue(), 8.70, DELTA);
    }

    public LayBackBetOption createBetOption(BigDecimal price, BetExchangeType betExchangeType, BettingPage bettingPage) {
        DefaultMoneyLineBetOption moneyLineBetOption = new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());
        return new LayBackBetOption<>(moneyLineBetOption, betExchangeType, price);
    }
}
