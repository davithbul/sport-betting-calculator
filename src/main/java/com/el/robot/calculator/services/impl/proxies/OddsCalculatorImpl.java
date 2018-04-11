package com.el.robot.calculator.services.impl.proxies;

import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.services.OddsCalculator;
import com.el.robot.calculator.services.factories.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Primary
@Service
public class OddsCalculatorImpl implements OddsCalculator {

    @Autowired
    private ServiceFactory serviceFactory;

    @Override
    public boolean isWinWin(BetOptionGroup betOptionGroup) {
        OddsCalculator oddsCalculator = serviceFactory.getService(betOptionGroup.getClass(), OddsCalculator.class);
        return oddsCalculator.isWinWin(betOptionGroup);
    }

    @Override
    public BigDecimal calculateWinPercentile(BetOptionGroup betOptionGroup) {
        OddsCalculator oddsCalculator = serviceFactory.getService(betOptionGroup.getClass(), OddsCalculator.class);
        return oddsCalculator.calculateWinPercentile(betOptionGroup);
    }
}
