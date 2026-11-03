import java.util.Scanner;

public class Lab6Calculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Add  2) Subtract  3) Multiply  4) Divide  0) Exit");
            System.out.print("Choice: ");
            int choice = in.nextInt();

            if (choice == 0) {
                running = false;
                continue;
            }
            if (choice < 1 || choice > 4) {
                System.out.println("Unknown option");
                continue;
            }

            System.out.print("First number: ");
            double a = in.nextDouble();
            System.out.print("Second number: ");
            double b = in.nextDouble();

            switch (choice) {
                case 1 -> System.out.println("Result: " + (a + b));
                case 2 -> System.out.println("Result: " + (a - b));
                case 3 -> System.out.println("Result: " + (a * b));
                case 4 -> {
                    if (b == 0) {
                        System.out.println("Cannot divide by zero");
                    } else {
                        System.out.println("Result: " + (a / b));
                    }
                }
            }
        }
        System.out.println("Goodbye");
        in.close();
    }
}
