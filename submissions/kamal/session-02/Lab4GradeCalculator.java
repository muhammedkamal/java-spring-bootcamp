import java.util.Scanner;

// Lab 4: Read 3 exam marks (0-100 each), print the average and the grade.
// Grades: >= 90 A, >= 75 B, >= 60 C, otherwise Fail.
// Check: 80 90 100 -> 90.00 A
// Reject any mark outside 0-100 with a message.
public class Lab4GradeCalculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read three marks
        System.out.print("Enter First Mark: ");
        int a = in.nextInt();
        while (a < 0 || a > 100){
            System.out.print("Enter a valid First Mark between 0 - 100: ");
            a = in.nextInt();
        }
        System.out.print("Enter Second Mark: ");
        int b= in.nextInt();
        while (b < 0 || b > 100){
            System.out.print("Enter a valid Second Mark between 0 - 100: ");
            b = in.nextInt();
        }
        in.nextLine();
        System.out.print("Enter Third Mark: ");
        int c = in.nextInt();
        while (c < 0 || c > 100){
            System.out.print("Enter a valid Third Mark between 0 - 100: ");
            c = in.nextInt();
        }
        // TODO: validate, compute average (use a double!)
        double avg = (a + b + c) / 3.0;
        // TODO: print average with 2 decimals and the grade
        System.out.printf("Avg: %.2f%n",avg);
        if (avg >= 90) {
            System.out.println("Grade: A");
        } else if (avg >= 75) {
            System.out.println("Grade: B");
        } else if (avg >= 60) {
            System.out.println("Grade: C");
        } else {
            System.out.println("Grade: Fail");
        }
        in.close();
    }
}
