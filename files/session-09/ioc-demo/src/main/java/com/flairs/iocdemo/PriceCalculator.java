package com.flairs.iocdemo;

// A plain class: no Spring annotation. AppConfig registers it as a bean with @Bean.
// You use @Bean for classes you do not own (a library class) or when creation needs logic.
public class PriceCalculator {

    private final double taxRate;

    public PriceCalculator(double taxRate) {
        this.taxRate = taxRate;
    }

    public double withTax(double price) {
        return Math.round(price * (1 + taxRate) * 100.0) / 100.0;
    }
}
