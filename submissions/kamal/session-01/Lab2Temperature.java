import java.util.Scanner;

// Lab 2: Read a temperature in Celsius and print it in Fahrenheit.
// Formula: F = C * 9 / 5 + 32      Check: 100 C -> 212.0 F, 37 C -> 98.6 F
public class Lab2Temperature {
    public static void main(String[] args) {
        System.out.print("Enter the temperature with Celsius: ");
        Scanner in = new Scanner(System.in);
        double celsius = in.nextDouble();
        double result = celsius * 9 / 5 + 32;
        System.out.printf("Result with Fahrenheit: %.1f%n",result);
    }
}
