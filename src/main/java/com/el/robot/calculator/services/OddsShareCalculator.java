package com.el.robot.calculator.services;

import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroupShare;

public interface OddsShareCalculator<betOptionGroup extends BetOptionGroup, betOptionGroupShare extends BetOptionGroupShare> {

    /**
     * Calculates the best stake's shares for each odd, for having the most equal and possible
     * chance of winning. Shares present percent of odds' stake. Taking into account also
     * bookmakers taxes.
     * @param betOptionGroup the group of bet options
     * @return shares for each odd
     */
    betOptionGroupShare calculateOddsShare(betOptionGroup betOptionGroup);
}
