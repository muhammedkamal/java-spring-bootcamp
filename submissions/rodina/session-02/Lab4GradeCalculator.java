import java.util.Scanner;

// Lab 4: Read 3 exam marks (0-100 each), print the average and the grade.
// Grades: >= 90 A, >= 75 B, >= 60 C, otherwise Fail.
// Check: 80 90 100 -> 90.00 A
// Reject any mark outside 0-100 with a message.
public class Lab4GradeCalculator {


    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter three marks: ");

        int mark1 = input.nextInt();

        int mark2 = input.nextInt();

        int mark3 = input.nextInt();

        if (mark1 < 0 || mark1 > 100 ||

                mark2 < 0 || mark2 > 100 ||

                mark3 < 0 || mark3 > 100) {

            System.out.println("Invalid marks");

        } else {

            double average = (mark1 + mark2 + mark3) / 3.0;

            System.out.printf("Average: %.2f%n", average);

            if (average >= 90) {

                System.out.println("Grade: A");

            } else if (average >= 75) {

                System.out.println("Grade: B");

            } else if (average >= 60) {

                System.out.println("Grade: C");

            } else {

                System.out.println("Fail");

            }

        }

        input.close();

    }

}
