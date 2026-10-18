import java.util.Scanner;

// Lab 5: Is a number prime? Then print all primes up to 50.
// Check: 97 prime, 1 not prime, 2 prime
// Hint: try divisors d from 2 while d * d <= n.
public class Lab5IsPrime {
    static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int d = 2; d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a number:");
        int n = in.nextInt();
        if (isPrime(n)) {
            System.out.println(n + " is prime");
        } else {
            System.out.println(n + " is not prime");
        }
        System.out.println("Prime numbers from 2 to 50:");
        for (int i = 2; i <= 50; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        in.close();
    }
}