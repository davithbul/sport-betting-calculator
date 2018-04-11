package com.el.robot.calculator.services.impl.proxies;

import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.exceptions.NotWinWinException;
import com.el.robot.calculator.services.BetCalculator;
import com.el.robot.calculator.services.factories.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class BetCalculatorImpl implements BetCalculator {

    @Autowired
    private ServiceFactory serviceFactory;

    @Override
    public BetGroup calculateBet(BetOptionGroup betOptionGroup, Stake fund) throws NotWinWinException {
        if (!serviceFactory.containsService(betOptionGroup.getClass())) {
            throw new RuntimeException("Bet Calculator for " + betOptionGroup.getClass().getSimpleName() + " hasn't been found.");
        }

        BetCalculator betCalculator = serviceFactory.getService(betOptionGroup.getClass(), BetCalculator.class);
        return betCalculator.calculateBet(betOptionGroup, fund);
    }

    @Override
    public boolean isWorthBetting(BetOptionGroup betOptionGroup) {
        if (!serviceFactory.containsService(betOptionGroup.getClass())) {
            throw new RuntimeException("Bet Calculator for " + betOptionGroup.getClass().getSimpleName() + " hasn't been found.");
        }

        BetCalculator betCalculator = serviceFactory.getService(betOptionGroup.getClass(), BetCalculator.class);
        return betCalculator.isWorthBetting(betOptionGroup);
    }
}
