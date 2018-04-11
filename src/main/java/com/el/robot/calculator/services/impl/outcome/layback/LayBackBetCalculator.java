package com.el.robot.calculator.services.impl.outcome.layback;

import com.el.betting.sdk.v2.BetExchangeType;
import com.el.betting.sdk.v2.BetPrice;
import com.el.betting.sdk.v2.betoption.api.Bet;
import com.el.betting.sdk.v2.betoption.bettype.layback.LayBackBet;
import com.el.betting.sdk.v2.betoption.bettype.layback.LayBackBetOption;
import com.el.betting.sdk.v2.builders.BetBuilder;
import com.el.betting.sdk.v2.ProfitSize;
import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.LayBackBetOptionGroupShare;
import com.el.robot.calculator.exceptions.NotWinWinException;
import com.el.robot.calculator.services.BetCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LayBackBetCalculator implements BetCalculator<LayBackBetOptionGroup, BetGroup<LayBackBet, LayBackBetOption>> {

    private final static int SCALE = 5;

    private LayBackOddsCalculator oddsCalculator;

    private LayBackOddsShareCalculator oddsShareCalculator;

    @Autowired
    public LayBackBetCalculator(LayBackOddsCalculator oddsCalculator, @Qualifier("equalLayBackShare") LayBackOddsShareCalculator oddsShareCalculator) {
        this.oddsCalculator = oddsCalculator;
        this.oddsShareCalculator = oddsShareCalculator;
    }

    @Override
    public BetGroup<LayBackBet, LayBackBetOption> calculateBet(LayBackBetOptionGroup betOptionGroup, Stake fund) {
        LayBackBetOptionGroupShare groupShare = oddsShareCalculator.calculateOddsShare(betOptionGroup);
        BetGroup<LayBackBet, LayBackBetOption> betGroup = new BetGroup<>(betOptionGroup.getBackBetOption(), betOptionGroup.getLayBetOption());
        BigDecimal fundAmount = fund.getAmount();

        //calculate back stake
        BigDecimal backBetStake = fundAmount.multiply(groupShare.getBackBetShare());
        Stake backStake = new Stake(backBetStake.setScale(0, RoundingMode.HALF_UP), fund.getCurrency());
        LayBackBet<LayBackBetOption> backBet = new LayBackBet<>(betOptionGroup.getBackBetOption(), BetExchangeType.BACK, new BetPrice(backStake.getAmount(), BigDecimal.valueOf(1000)), backStake);
        betGroup.setBet(1, backBet);


        //calculate lay stake
        BigDecimal layBetUserStake = fundAmount.multiply(groupShare.getLayUserBetShare());
        Stake layUserStake = new Stake(layBetUserStake, fund.getCurrency());
        LayBackBet<LayBackBetOption> layBet = new LayBackBet<>(betOptionGroup.getBackBetOption(), BetExchangeType.LAY, new BetPrice(layBetUserStake, BigDecimal.valueOf(1000)), layUserStake);

        //calculate lay user stake
        BigDecimal layBetStake = fundAmount.multiply(groupShare.getLayBetShare());
        Stake layStake = new Stake(layBetStake, fund.getCurrency());
        layBet.addProperty("layUserStake", layStake);

        //calculate lay liability
        BigDecimal layLiabilityStake = fundAmount.multiply(groupShare.getLiabilityShare());
        layBet.addProperty("layLiability", layLiabilityStake);
        betGroup.setBet(2, layBet);

        return betGroup;
    }

    @Override
    public boolean isWorthBetting(LayBackBetOptionGroup betOptionGroup) {
        BigDecimal bigDecimal = oddsCalculator.calculateWinPercentile(betOptionGroup);
        ProfitSize profitSize = ProfitSize.getSize(bigDecimal.doubleValue());
        return profitSize.isWorthBetting();
    }
}
