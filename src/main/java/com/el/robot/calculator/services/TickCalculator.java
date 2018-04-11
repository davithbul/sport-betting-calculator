package com.el.robot.calculator.services;

import java.math.BigDecimal;

public class TickCalculator {

    public static int calculateTick(double lowestOdd, double highestOdd) {
        double difference = highestOdd - lowestOdd;
        int tickRate = getTickRate(highestOdd);
        return (int) (difference * tickRate);
    }

    public static int calculateTick(BigDecimal lowestOdd, BigDecimal highestOdd) {
        final BigDecimal difference = highestOdd.subtract(lowestOdd);
        int tickRate = getTickRate(highestOdd.doubleValue());
        return difference.multiply(BigDecimal.valueOf(tickRate)).intValue();
    }

    public static int getTickRate(double odd) {
        if(odd >= 1.01 && odd <= 2) {
            return (int) (odd * 100);
        }

        return 1;
    }
}
