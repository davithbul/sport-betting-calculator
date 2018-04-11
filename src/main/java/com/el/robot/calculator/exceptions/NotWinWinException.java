package com.el.robot.calculator.exceptions;


import com.el.betting.sdk.v3.betoption.group.BetOptionGroup;

public class NotWinWinException extends BetCalculationException {

    private BetOptionGroup betOptionGroup;
    private String message;

    public NotWinWinException(BetOptionGroup betOptionGroup) {
        this.betOptionGroup = betOptionGroup;
    }

    public NotWinWinException(BetOptionGroup betOptionGroup, String message) {
        super(message);
        this.betOptionGroup = betOptionGroup;
    }
}
