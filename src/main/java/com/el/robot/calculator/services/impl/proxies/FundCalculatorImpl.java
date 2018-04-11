package com.el.robot.calculator.services.impl.proxies;

import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.services.FundCalculator;
import com.el.robot.calculator.services.factories.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@SuppressWarnings("unchecked")
public class FundCalculatorImpl implements FundCalculator {

    @Autowired
    private ServiceFactory serviceFactory;

    @Override
    public Stake calculateFund(BetOptionGroup betOptionGroup) {
        FundCalculator fundCalculator = serviceFactory.getService(betOptionGroup.getClass(), FundCalculator.class);
        return fundCalculator.calculateFund(betOptionGroup);
    }

    @Override
    public Stake calculateOptimalFund(BetOptionGroup betOptionGroup) {
        FundCalculator fundCalculator = serviceFactory.getService(betOptionGroup.getClass(), FundCalculator.class);
        return fundCalculator.calculateOptimalFund(betOptionGroup);
    }
}
