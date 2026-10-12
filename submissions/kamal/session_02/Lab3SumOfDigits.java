package kamal.session_02;

import java.util.Scanner;

// Lab 3: Read a positive int and print the sum of its digits.
// Check: 4821 -> 15
// Hint: while (n > 0) { take n % 10, then n = n / 10 }
public class Lab3SumOfDigits {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read n
        System.out.print("Enter a positive int: ");
        int n = in.nextInt();
        // TODO: loop while n > 0, add the last digit, drop it
        int sum = 0;
        while (n >0){
            int x = n % 10;
            sum += x;
            n /= 10;
        }
        System.out.println("sum of its digits: " + sum);
        in.close();
    }
}
