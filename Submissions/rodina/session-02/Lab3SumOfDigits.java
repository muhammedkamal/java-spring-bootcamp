import jdk.internal.io.JdkConsoleImpl;

import java.util.Scanner;

// Lab 3: Read a positive int and print the sum of its digits.
// Check: 4821 -> 15
// Hint: while (n > 0) { take n % 10, then n = n / 10 }
public class Lab3SumOfDigits {
    public static void main(String[] args) {
        Scanner input
                = new Scanner(System.in);
        System.out.println("Enter a Number");
        int number = input.nextInt();
        number = Math.abs(number);
        int sum = 0;
        while (number < 0) {
            int digit = number % 10;
            sum = sum + digit;
            number = number / 10;
        }
        System.out.println("sum =" + sum);
        input.close();
    }

}