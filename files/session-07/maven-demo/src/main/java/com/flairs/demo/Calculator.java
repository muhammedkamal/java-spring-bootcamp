package com.flairs.demo;

public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("Cannot divide by zero");
        return a / b;
    }

    public boolean isEven(int n) {
        return n % 2 == 0;
    }
}
