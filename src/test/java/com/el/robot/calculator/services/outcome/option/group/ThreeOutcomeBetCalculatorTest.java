package com.el.robot.calculator.services.outcome.option.group;

import com.el.betting.sdk.v2.*;import com.el.betting.sdk.v4.*;
import com.el.betting.sdk.v2.betoption.api.BetOption;
import com.el.betting.sdk.v2.betoption.bettype.moneyline.DefaultMoneyLineBetOption;
import com.el.betting.sdk.v2.pages.WebBettingPage;
import com.el.betting.sdk.v2.provider.Bookmaker;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.exceptions.NotWinWinException;
import com.el.robot.calculator.services.BetCalculator;
import com.el.robot.calculator.services.OddsShareCalculator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ThreeOutcomeBetCalculatorTest extends SpringJUnitTest {

    private final static double DELTA = 0.004;

    @Autowired
    private BetCalculator betCalculator;

    @Autowired
    private OddsShareCalculator oddsShareCalculator;

    @Test
    public void testBetCalculation() throws NotWinWinException {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.5));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25));
        BetOptionGroup<BetOption> betOptionGroup = new BetOptionGroup<>(betOption1, betOption2, betOption3);

        BigDecimal amount = BigDecimal.valueOf(1000);
        Stake fund = new Stake(amount, Currency.of("EUR"));
        BetGroup betGroup = betCalculator.calculateBet(betOptionGroup, fund);

        BetOptionGroupShare betOptionGroupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);
        BigDecimal homeTeamBetAmount = betOptionGroupShare.getBetShare(1).multiply(amount);
        BigDecimal drawBetAmount = betOptionGroupShare.getBetShare(2).multiply(amount);
        BigDecimal awayTeamBetAmount = betOptionGroupShare.getBetShare(3).multiply(amount);

        assertEquals(homeTeamBetAmount.doubleValue(), betGroup.getBet(1).getStake().getAmount().doubleValue(), DELTA);
        assertEquals(drawBetAmount.doubleValue(), betGroup.getBet(2).getStake().getAmount().doubleValue(), DELTA);
        assertEquals(awayTeamBetAmount.doubleValue(), betGroup.getBet(3).getStake().getAmount().doubleValue(), DELTA);
    }

    @Test(expected = NotWinWinException.class)
    public void testNotWinWinBetCalculation() throws NotWinWinException {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(2.5));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(3.3));
        BetOption betOption3 = createBetOption(BigDecimal.valueOf(3.25));
        BetOptionGroup<BetOption> betOptionGroup = new BetOptionGroup<>(betOption1, betOption2, betOption3);

        Stake fund = new Stake(BigDecimal.valueOf(1000), Currency.of("EUR"));
        betCalculator.calculateBet(betOptionGroup, fund);
    }

    public BetOption createBetOption(BigDecimal price) {
        WebBettingPage bettingPage = new WebBettingPage(Bookmaker.Bet365, null, null);
        return new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());
    }
}
