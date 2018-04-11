package com.el.robot.calculator.services;


import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;

/**
 * FundCalculator calculates overall money, which required
 * for betting. If it's 2 outcome, or lay back betting,
 * than fund will be sum of 2 bet stakes.
 * For 3 outcome game odds, it should be sum of 3 bet stakes.
 * @param <betOptionGroup>
 */
public interface FundCalculator<betOptionGroup extends BetOptionGroup> {

    Stake calculateFund(final betOptionGroup betOptionGroup);

    Stake calculateOptimalFund(final betOptionGroup betOptionGroup);
}
