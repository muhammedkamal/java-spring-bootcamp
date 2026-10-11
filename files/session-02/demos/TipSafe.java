import java.util.Scanner;

// Fixes the "people = 0" problem from TipCalculator in Session 1A.
public class TipSafe {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Bill (EGP): ");
        double bill = in.nextDouble();
        System.out.print("People: ");
        int people = in.nextInt();

        if (people <= 0) {
            System.out.println("People must be at least 1");
        } else {
            System.out.printf("Each pays: %.2f%n", bill / people);
        }
        in.close();
    }
}
