import java.util.Scanner;

public class Lab2Temperature {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Celsius: ");
        double celsius = in.nextDouble();

        double fahrenheit = celsius * 9 / 5 + 32; // celsius is double, so no integer division

        System.out.printf("%.1f C = %.1f F%n", celsius, fahrenheit);

        in.close();
    }
}
