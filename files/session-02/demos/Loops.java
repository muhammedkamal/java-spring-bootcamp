// The three loop forms. Predict the output before running.
public class Loops {
    public static void main(String[] args) {
        // for: you know how many times
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // while: repeat while a condition holds
        int n = 1;
        while (n <= 100) {
            n = n * 2;
        }
        System.out.println("First power of 2 above 100: " + n);

        // do-while: body runs at least once
        int k = 10;
        do {
            System.out.println("k = " + k);
            k++;
        } while (k < 5);
    }
}
