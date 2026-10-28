package com.flairs.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {
    private Calculator calc;

    @BeforeEach
    void setUp() {                      // runs before every test: each test gets a fresh object
        calc = new Calculator();
    }

    @Test
    @DisplayName("add returns the sum")
    void addReturnsSum() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void divideByZeroThrows() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> calc.divide(1, 0));
        assertEquals("Cannot divide by zero", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "10, 2, 5", "9, 3, 3", "7, 2, 3", "-8, 2, -4" })
    void divideCases(int a, int b, int expected) {
        assertEquals(expected, calc.divide(a, b));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 2, 100, -4 })
    void evenNumbers(int n) {
        assertTrue(calc.isEven(n));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 3, 99, -7 })
    void oddNumbers(int n) {
        assertFalse(calc.isEven(n));
    }
}
