package com.el.robot.calculator.services;

import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;

import java.math.BigDecimal;

public interface OddsCalculator<betOptionGroup extends BetOptionGroup> {

    /**
     * The win win situation you will have when
     * 1/odd1 + 1/odd2 + 1/odd3 < 1, that's why we are
     * calculating aliquot for all bet amounts
     * in order to escape real numbers.
     *
     * @return true if there is win win situation otherwise false
     */
    boolean isWinWin(betOptionGroup betOptionGroup);

    /**
     * Returns number between 0-100 which shows
     * the win percentile.
     *
     * @return returns winning percentile, could be negative, if the game is not win-win
     */
    BigDecimal calculateWinPercentile(betOptionGroup betOptionGroup);
}
