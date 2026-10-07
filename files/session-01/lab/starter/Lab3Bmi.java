import java.util.Scanner;

// Lab 3: Read weight (kg) and height (m), print the BMI.
// Formula: BMI = weight / (height * height)     Check: 70 kg, 1.75 m -> 22.86
public class Lab3Bmi {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read weight and height as doubles
        System.out.print("Enter Weight :");
        double weight = in.nextDouble();
        in.nextLine();
        System.out.print("Enter height :");
        double height = in.nextDouble();
        // TODO: compute and print the BMI with 2 decimal places
        double BMI = weight / (height * height);
        System.out.printf("%.2f%n",BMI);
    }
}
