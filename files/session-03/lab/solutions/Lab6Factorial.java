import java.util.Scanner;

public class Lab6Factorial {
    static long factorialLoop(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    static long factorialRecursive(int n) {
        if (n <= 1) {
            return 1;                       // base case
        }
        return n * factorialRecursive(n - 1);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("n (0 to 20): ");
        int n = in.nextInt();
        if (n < 0 || n > 20) {
            System.out.println("Out of range");
        } else {
            System.out.println("loop      : " + factorialLoop(n));
            System.out.println("recursion : " + factorialRecursive(n));
        }
        in.close();
    }
}
