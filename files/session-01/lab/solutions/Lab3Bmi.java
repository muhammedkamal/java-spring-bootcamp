import java.util.Scanner;

public class Lab3Bmi {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Weight (kg): ");
        double weight = in.nextDouble();
        System.out.print("Height (m): ");
        double height = in.nextDouble();

        double bmi = weight / (height * height);

        System.out.printf("BMI: %.2f%n", bmi);
    }
}
