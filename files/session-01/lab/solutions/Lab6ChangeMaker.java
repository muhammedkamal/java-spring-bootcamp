import java.util.Scanner;

public class Lab6ChangeMaker {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Amount (EGP): ");
        int amount = in.nextInt();

        int n200 = amount / 200; amount = amount % 200;
        int n100 = amount / 100; amount = amount % 100;
        int n50  = amount / 50;  amount = amount % 50;
        int n20  = amount / 20;  amount = amount % 20;
        int n10  = amount / 10;  amount = amount % 10;
        int n5   = amount / 5;   amount = amount % 5;
        int n1   = amount;

        System.out.println("200 x" + n200 + ", 100 x" + n100 + ", 50 x" + n50
            + ", 20 x" + n20 + ", 10 x" + n10 + ", 5 x" + n5 + ", 1 x" + n1);
        // In Session 1B we rewrite this with a loop over an array of notes.
    }
}
