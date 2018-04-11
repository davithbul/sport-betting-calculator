package com.el.robot.calculator.services.outcome.option.group;

import com.el.betting.sdk.v2.OddsFormat;
import com.el.betting.sdk.v2.Period;
import com.el.betting.sdk.v2.Team;
import com.el.betting.sdk.v2.betoption.api.BetOption;
import com.el.betting.sdk.v2.betoption.bettype.moneyline.DefaultMoneyLineBetOption;
import com.el.betting.sdk.v2.pages.AffiliateBettingPage;
import com.el.betting.sdk.v2.pages.BettingPage;
import com.el.betting.sdk.v2.pages.WebBettingPage;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.services.impl.outcome.group.BetOptionGroupShareCalculator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.common.SpringJUnitTest;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.el.betting.sdk.v2.provider.Bookmaker.FIVE_PERCENT_AFFILIATE;
import static com.el.betting.sdk.v2.provider.Bookmaker.ZERO_PERCENT_AFFILIATE;
import static org.junit.Assert.assertEquals;

public class TwoOutcomeShareTest extends SpringJUnitTest {

    private final static double DELTA = 0.004;

    @Autowired
    private BetOptionGroupShareCalculator betOptionGroupOddsShareCalculator;

    @Test
    public void affiliateSimpleTest() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(1.5), new AffiliateBettingPage(FIVE_PERCENT_AFFILIATE));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(2.5), new AffiliateBettingPage(ZERO_PERCENT_AFFILIATE));

        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2);
        BetOptionGroupShare betOptionShareGroup = betOptionGroupOddsShareCalculator.calculateOddsShare(betOptionGroup);

        BigDecimal team1Win = betOptionShareGroup.getBetShare(1).multiply(betOptionShareGroup.getPrice(1)).multiply(BigDecimal.valueOf(0.95))
                .add(betOptionShareGroup.getBetShare(2).multiply(BigDecimal.valueOf(0.2)));

        BigDecimal team2Win = betOptionShareGroup.getBetShare(2).multiply(betOptionShareGroup.getPrice(2))
                .add(betOptionShareGroup.getBetShare(1).multiply(BigDecimal.valueOf(0.25)));

        assertEquals(team1Win.doubleValue(), team2Win.doubleValue(), DELTA);
    }

    @Test
    public void withoutAffiliateSimpleTest() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(1.5), new WebBettingPage(FIVE_PERCENT_AFFILIATE));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(2.5), new WebBettingPage(ZERO_PERCENT_AFFILIATE));

        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2);
        BetOptionGroupShare betOptionShareGroup = betOptionGroupOddsShareCalculator.calculateOddsShare(betOptionGroup);

        BigDecimal team1Win = betOptionShareGroup.getBetShare(1).multiply(betOptionShareGroup.getPrice(1)).multiply(BigDecimal.valueOf(0.95));
        BigDecimal team2Win = betOptionShareGroup.getBetShare(2).multiply(betOptionShareGroup.getPrice(2));

        assertEquals(team1Win.doubleValue(), team2Win.doubleValue(), DELTA);
    }

    @Test
    public void withOneAffiliateSimpleTest() {
        BetOption betOption1 = createBetOption(BigDecimal.valueOf(1.5), new AffiliateBettingPage(FIVE_PERCENT_AFFILIATE));
        BetOption betOption2 = createBetOption(BigDecimal.valueOf(2.5), new WebBettingPage(ZERO_PERCENT_AFFILIATE));

        BetOptionGroup betOptionGroup = new BetOptionGroup(betOption1, betOption2);
        BetOptionGroupShare betOptionShareGroup = betOptionGroupOddsShareCalculator.calculateOddsShare(betOptionGroup);

        BigDecimal team1Win = betOptionShareGroup.getBetShare(1).multiply(betOptionShareGroup.getPrice(1)).multiply(BigDecimal.valueOf(0.95));

        BigDecimal team2Win = betOptionShareGroup.getBetShare(2).multiply(betOptionShareGroup.getPrice(2))
                .add(betOptionShareGroup.getBetShare(1).multiply(BigDecimal.valueOf(0.25)));


        assertEquals(team1Win.doubleValue(), team2Win.doubleValue(), DELTA);
    }

    public BetOption createBetOption(BigDecimal price, BettingPage bettingPage) {
        return new DefaultMoneyLineBetOption(null, 0, null, null, Period.MATCH, new Team("REAL", Team.Side.HOME), price, OddsFormat.DECIMAL, bettingPage,
                null, null, new HashMap<>());

    }
}
