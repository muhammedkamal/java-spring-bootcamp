import java.util.Scanner;

// Lab 5: Is a number prime? Then print all primes up to 50.
// Check: 97 prime, 1 not prime, 2 prime
// Hint: try divisors d from 2 while d * d <= n.
public class Lab5IsPrime {
    // TODO: static boolean isPrime(int n)
    public static boolean isPrime(int n){
        if (n < 2) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read n and print the verdict
        System.out.print("Enter a number: ");
        int n = in.nextInt();
        System.out.println(isPrime(n));
        // TODO: print every prime from 2 to 50 using isPrime
        for (int i = 2; i <= 50 ; i++){
            if (isPrime(i)){
                System.out.println(i);
            }
        }
        in.close();
    }

}
