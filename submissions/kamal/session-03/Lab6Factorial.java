    import java.util.Scanner;

    // Lab 6: Factorial two ways, a loop and recursion.
    // Check: 0 -> 1, 10 -> 3628800, 20 -> 2432902008176640000
    // Use long. Reject n < 0 or n > 20 with "Out of range".
    public class Lab6Factorial {
        // TODO: static long factorialLoop(int n)
        // TODO: static long factorialRecursive(int n)   // base case first!

        public static void main(String[] args) {
            Scanner in = new Scanner(System.in);
            // TODO: read n, validate, print both results
            System.out.print("Write a number: ");
            int n = in.nextInt();
            if (n < 0 || n > 20) {
                System.out.println("Out of range");
            } else {
                System.out.println(factorialLoop(n));
                System.out.println(factorialRecursive(n));
            }
            in.close();
        }
        public static long factorialLoop(int n){
            long sum = 1;
            for (int i = 1; i <= n; i++){
                sum *= i;
            }
            return sum;
        }
        public static long factorialRecursive(int n){
            if (n == 0) return 1;
            return n * factorialRecursive(n - 1 );
        }
    }