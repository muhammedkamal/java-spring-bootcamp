import java.util.Scanner;

// Live coding: decisions with if / else if / else.
public class IfElse {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Score (0-100): ");
        int score = in.nextInt();

        if (score < 0 || score > 100) {
            System.out.println("Invalid score");
        } else if (score >= 90) {
            System.out.println("A");
        } else if (score >= 75) {
            System.out.println("B");
        } else if (score >= 60) {
            System.out.println("C");
        } else {
            System.out.println("Fail");
        }
        in.close();
    }
}
