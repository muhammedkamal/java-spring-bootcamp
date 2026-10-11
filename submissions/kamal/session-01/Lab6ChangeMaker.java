import java.util.Scanner;

// Lab 6 (stretch): Read an amount in EGP and print the fewest notes/coins.
// Denominations: 200, 100, 50, 20, 10, 5, 1
// Check: 387 -> 200 x1, 100 x1, 50 x1, 20 x1, 10 x1, 5 x1, 1 x2
// Hint: count = amount / 200; amount = amount % 200; then the next note...
// (No loops yet: we learn them on Wednesday. Repeat the two lines for each note.)
public class Lab6ChangeMaker {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int amount = in.nextInt();

        int notes200 = amount / 200;
        amount %= 200;

        int notes100 = amount / 100;
        amount %= 100;

        int notes50 = amount / 50;
        amount %= 50;

        int notes20 = amount / 20;
        amount %= 20;

        int notes10 = amount / 10;
        amount %= 10;

        int notes5 = amount / 5;
        amount %= 5;

        int notes1 = amount;

        System.out.printf(
                "200 x%d, 100 x%d, 50 x%d, 20 x%d, 10 x%d, 5 x%d, 1 x%d%n",
                notes200, notes100, notes50, notes20,
                notes10, notes5, notes1
        );
    }
}
