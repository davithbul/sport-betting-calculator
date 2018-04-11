/**
 * Provides services for bet and odds calculations.
 * There are 3 central services which are providing basic calculations.
 * <p>
 * {@link com.el.robot.calculator.services.OddsShareCalculator} - calculates shares for each betOption
 * <p>
 * {@link com.el.robot.calculator.services.OddsCalculator} - calculates win percentile based on given odds
 * <p>
 * {@link com.el.robot.calculator.services.FundCalculator} - calculates funds which should be used as a stake for a bet
 * <p>
 * {@link com.el.robot.calculator.services.BetCalculator} - calculates bets based on given betOptions and available stake
 * <p>
 * Multiple implementation are available under {@link com.el.robot.calculator.services.impl} package which could be
 * accessed using ServiceFactory mechanism.
 * {@link com.el.robot.calculator.services.factories.ServiceFactory} registers and provides default implementation for the services.
 */
package com.el.robot.calculator;
