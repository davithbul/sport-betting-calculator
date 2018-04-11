package com.el.robot.calculator.services.impl.outcome.group;

import com.el.betting.sdk.v2.ProfitSize;
import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v2.betoption.api.Bet;
import com.el.betting.sdk.v2.builders.BetBuilder;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.exceptions.NotWinWinException;
import com.el.robot.calculator.services.BetCalculator;
import com.el.robot.calculator.services.OddsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BetGroupCalculator implements BetCalculator<BetOptionGroup, BetGroup> {

    @Autowired
    private OddsCalculator oddsCalculator;

    @Autowired
    private BetOptionGroupShareCalculator oddsShareCalculator;

    /**
     * Calculates win win situation and splits amount
     * between the teams equally.
     *
     * @param betOptionGroup based on this bet should be calculated
     * @param fund
     * @return
     * @throws NotWinWinException when with the current gameOdds isn't win win
     */
    public BetGroup calculateBet(BetOptionGroup betOptionGroup, Stake fund) throws NotWinWinException {
        if (!oddsCalculator.isWinWin(betOptionGroup)) {
            throw new NotWinWinException(betOptionGroup);
        }

        BetOptionGroupShare betOptionShareGroup = oddsShareCalculator.calculateOddsShare(betOptionGroup);

        BetGroup betGroup = new BetGroup(betOptionGroup);
        BigDecimal fundAmount = fund.getAmount();

        //calculate bets
        for (int i = 1; i <= betOptionGroup.getBetOptions().length; i++) {
            BigDecimal betAmount = fundAmount.multiply(betOptionShareGroup.getBetShare(i));
            Bet bet = BetBuilder.buildBet(betOptionShareGroup.getBetOption(i), new Stake(betAmount, fund.getCurrency()));
            betGroup.setBet(i, bet);
        }

        return betGroup;
    }

    /**
     * {@inheritDoc}
     *
     * @param betOptionGroup
     * @return
     */
    public boolean isWorthBetting(BetOptionGroup betOptionGroup) {
        BigDecimal bigDecimal = oddsCalculator.calculateWinPercentile(betOptionGroup);
        ProfitSize profitSize = ProfitSize.getSize(bigDecimal.doubleValue());
        return profitSize.isWorthBetting();
    }
}
