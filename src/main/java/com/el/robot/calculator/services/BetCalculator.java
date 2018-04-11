package com.el.robot.calculator.services;

import com.el.betting.sdk.v2.Stake;
import com.el.betting.sdk.v3.betoption.group.BetGroup;
import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;
import com.el.robot.calculator.exceptions.NotWinWinException;

/**
 * Bet Calculator calculates bets based on given game odds. Different bookmakers can have different
 * confidence level,different tax policy, and the implementers of this interface should take into
 * account all of teh drawbacks.
 *
 * @param <betGroup> represents the type of the game odds (e.g. Three outcome game odds).
 */
public interface BetCalculator<betOptionGroup extends BetOptionGroup, betGroup extends BetGroup> {

    /**
     * Calculates the best bet amount which should be bet for
     * each of the team, in order to have win win situation.
     *
     * @param betOptionGroup represents the odds of the game
     * @param fund           represents the amount of money which is available for all of the bets together
     * @return the best bet amount for each team
     * @throws com.el.robot.calculator.exceptions.NotWinWinException when the current gameOdds isn't win win
     */
    betGroup calculateBet(final betOptionGroup betOptionGroup, final Stake fund) throws NotWinWinException;

    /**
     * Decides if it worth to bet or not, based on win size.
     * Confidence level of bookmaker and taxes could be considered.
     *
     * @param betOptionGroup represents the odds of the game
     * @return true if it's worth to bet having given odds
     */
    boolean isWorthBetting(final betOptionGroup betOptionGroup);
}
