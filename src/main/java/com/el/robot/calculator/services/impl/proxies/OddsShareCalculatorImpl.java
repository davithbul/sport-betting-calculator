package com.el.robot.calculator.services.impl.proxies;

import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;
import com.el.robot.calculator.services.OddsShareCalculator;
import com.el.robot.calculator.services.factories.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class OddsShareCalculatorImpl implements OddsShareCalculator {

    @Autowired
    private ServiceFactory serviceFactory;

    @Override
    public BetOptionGroupShare calculateOddsShare(BetOptionGroup betOptionGroup) {
        OddsShareCalculator oddsShareCalculator = serviceFactory.getService(betOptionGroup.getClass(), OddsShareCalculator.class);
        return oddsShareCalculator.calculateOddsShare(betOptionGroup);
    }
}
