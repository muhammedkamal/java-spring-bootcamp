import java.util.Scanner;

// Lab 4: Read 3 exam marks (0-100 each), print the average and the grade.
// Grades: >= 90 A, >= 75 B, >= 60 C, otherwise Fail.
// Check: 80 90 100 -> 90.00 A
// Reject any mark outside 0-100 with a message.
public class Lab4GradeCalculator {


    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter your grade: ");

        int grade = input.nextInt();

        if (grade < 0 || grade > 100) {

            System.out.println("Invalid grade");

        } else if (grade >= 90) {

            System.out.println("Grade: A");

        } else if (grade >= 80) {

            System.out.println("Grade: B");

        } else if (grade >= 70) {

            System.out.println("Grade: C");

        } else if (grade >= 60) {

            System.out.println("Grade: D");

        } else {

            System.out.println("Grade: F");

        }

        input.close();

    }

}

