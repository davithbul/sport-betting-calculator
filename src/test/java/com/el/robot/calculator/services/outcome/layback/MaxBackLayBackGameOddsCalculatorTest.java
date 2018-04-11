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
import com.el.robot.calculator.services.impl.outcome.layback.MaxBackOddsShareCalculator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.*;


public class MaxBackLayBackGameOddsCalculatorTest extends SpringJUnitTest {

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
    private MaxBackOddsShareCalculator oddsShareCalculator;

    @Autowired
    @Qualifier("equalLayBackShare")
    private LayBackOddsShareCalculator equalOddsShareCalculator;

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

        LayBackBetOptionGroupShare biasedShare = oddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);
        LayBackBetOptionGroupShare equalShare = equalOddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);

        assertEquals(equalShare.getBackBetShare(), biasedShare.getBackBetShare());
        assertEquals(equalShare.getLayBetShare(), biasedShare.getLayBetShare());
        assertEquals(equalShare.getLayUserBetShare(), biasedShare.getLayUserBetShare());
    }

    @Test
    public void testHigherBack() {
        LayBackBetOption backBetOption = createBetOption(BigDecimal.valueOf(2.5), BetExchangeType.BACK, withoutTaxWebBettingPage);
        LayBackBetOption layBetOption = createBetOption(BigDecimal.valueOf(2), BetExchangeType.LAY, withoutTaxWebBettingPage);
        LayBackBetOptionGroup layBackBetOptionGroup = new LayBackBetOptionGroup<>(backBetOption, layBetOption);

        LayBackBetOptionGroupShare groupShare = oddsShareCalculator.calculateOddsShare(layBackBetOptionGroup);
        assertEquals(groupShare.getLayUserBetShare(), groupShare.getBackBetShare());
        assertEquals(BigDecimal.ONE.doubleValue(), groupShare.getLayBetShare().add(groupShare.getBackBetShare()).doubleValue(), DELTA);
    }

    public LayBackBetOption createBetOption(BigDecimal price, BetExchangeType betExchangeType, BettingPage bettingPage) {
        DefaultMoneyLineBetOption moneyLineBetOption = new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());
        return new LayBackBetOption<>(moneyLineBetOption, betExchangeType, price);
    }
}
