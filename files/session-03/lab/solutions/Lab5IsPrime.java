import java.util.Scanner;

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
        System.out.print("Number: ");
        int n = in.nextInt();
        System.out.println(n + (isPrime(n) ? " is prime" : " is not prime"));
        System.out.print("Primes up to 50: ");
        for (int i = 2; i <= 50; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
        in.close();
    }
}
