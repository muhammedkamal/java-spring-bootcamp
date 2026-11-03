import java.util.Scanner;

// Lab 6: Factorial two ways, a loop and recursion.
// Check: 0 -> 1, 10 -> 3628800, 20 -> 2432902008176640000
// Use long. Reject n < 0 or n > 20 with "Out of range".
import java.util.Scanner;
public class Lab6Factorial {
    static long factorialLoop(int n) {
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    static long factorialRecursive(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a number:");
        int n = in.nextInt();
        if (n < 0 || n > 20) {
            System.out.println("Out of range");
        } else {
            System.out.println("Factorial using loop: " + factorialLoop(n));
            System.out.println("Factorial using recursion: " + factorialRecursive(n));
        }
        in.close();
    }
}