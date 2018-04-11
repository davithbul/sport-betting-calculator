package com.el.robot.calculator.services.impl.proxies;

import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v2.betoption.api.Bet;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.exceptions.NotWinWinException;
import com.el.robot.calculator.services.BetCalculator;
import com.el.robot.calculator.services.OddsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BetCalculatorRoundedStake implements BetCalculator{

    @Autowired
    private OddsCalculator oddsCalculator;

    @Autowired
    private BetCalculator betCalculator;

    public BetGroup calculateBet(BetOptionGroup betOptionGroup, Stake fund) throws NotWinWinException {
        //calculate bet and win percentile
        BigDecimal winPercentile = oddsCalculator.calculateWinPercentile(betOptionGroup);
        BetGroup betGroup = betCalculator.calculateBet(betOptionGroup, fund);

        //get stake with fraction proportions
        Bet[] bets = betGroup.getBets();
        TreeMap<StakeFraction, Bet> stakeFractionBettingInfoMap = new TreeMap<>();

        for (Bet bet : bets) {
            StakeFraction stakeFraction = new StakeFraction(bet.getStake().getAmount());
            stakeFractionBettingInfoMap.put(stakeFraction, bet);
        }

        //check if still win win
        StakeFraction highestStakeFraction = stakeFractionBettingInfoMap.firstKey();
        int middleToUp = highestStakeFraction.compareMiddle >= 0 ? 1 : -1;

        StakeFraction secondHighStakeFraction = stakeFractionBettingInfoMap.higherKey(highestStakeFraction);
        if (secondHighStakeFraction.fractionPercent.compareTo(winPercentile) > 0) {
            throw new NotWinWinException(betOptionGroup, "After rounding stakes the game stopped being win win!!!");
        }

        boolean moneyBalancePositive = true;
        for (Map.Entry<StakeFraction, Bet> betEntry : stakeFractionBettingInfoMap.entrySet()) {
            Bet bet = betEntry.getValue();
            StakeFraction stakeFraction = betEntry.getKey();
            switch (stakeFraction.compareMiddle * middleToUp) {
                case 0:
                    if (moneyBalancePositive) {
                        moneyBalancePositive = false;
                    } else {
                        bet.setStake(new Stake(BigDecimal.valueOf(stakeFraction.decimal), bet.getStake().getCurrency()));
                        break;
                    }
                case 1:
                    bet.setStake(new Stake(BigDecimal.valueOf(stakeFraction.decimal + 1), bet.getStake().getCurrency()));
                    break;
                case -1:
                    bet.setStake(new Stake(BigDecimal.valueOf(stakeFraction.decimal), bet.getStake().getCurrency()));
                    break;
            }
        }


        return betGroup;
    }

    @Override
    public boolean isWorthBetting(BetOptionGroup betOptionGroup) {
        return betCalculator.isWorthBetting(betOptionGroup);
    }

    public static class StakeFraction implements Comparable<StakeFraction> {
        final BigDecimal stake;
        final long decimal;
        final BigDecimal fraction;
        final BigDecimal fractionPercent;
        final int compareMiddle;

        StakeFraction(BigDecimal stake) {
            this.stake = stake;
            this.decimal = stake.longValue();
            this.fraction = stake.remainder(BigDecimal.ONE);
            this.fractionPercent = this.fraction.divide(this.stake, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100));
            compareMiddle = this.fraction.compareTo(BigDecimal.valueOf(0.5));
        }

        @Override
        public int compareTo(StakeFraction o) {
            int compareResult = o.fractionPercent.subtract(this.fractionPercent).compareTo(BigDecimal.ZERO);
            if(compareResult == 0) {
                compareResult = o.hashCode() - this.hashCode();
            }
            return compareResult;
        }
    }
}
