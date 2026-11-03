import java.util.Scanner;

public class Lab4GradeCalculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Mark 1: ");
        int m1 = in.nextInt();
        System.out.print("Mark 2: ");
        int m2 = in.nextInt();
        System.out.print("Mark 3: ");
        int m3 = in.nextInt();

        if (m1 < 0 || m1 > 100 || m2 < 0 || m2 > 100 || m3 < 0 || m3 > 100) {
            System.out.println("Marks must be between 0 and 100");
        } else {
            double avg = (m1 + m2 + m3) / 3.0;
            String grade;
            if (avg >= 90) {
                grade = "A";
            } else if (avg >= 75) {
                grade = "B";
            } else if (avg >= 60) {
                grade = "C";
            } else {
                grade = "Fail";
            }
            System.out.printf("Average: %.2f  Grade: %s%n", avg, grade);
        }
        in.close();
    }
}
