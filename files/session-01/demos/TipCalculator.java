import java.util.Scanner;

// Live coding: split a restaurant bill with a tip.
public class TipCalculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Bill (EGP): ");
        double bill = in.nextDouble();
        System.out.print("Tip %: ");
        int tipPercent = in.nextInt();
        System.out.print("People: ");
        int people = in.nextInt();

        double tip = bill * tipPercent / 100.0;
        double total = bill + tip;

        System.out.printf("Tip: %.2f%n", tip);
        System.out.printf("Total: %.2f%n", total);
        System.out.printf("Each pays: %.2f%n", total / people);
        // What if people is 0? We handle that with if/else in Session 1B.
        in.close();
    }
}
