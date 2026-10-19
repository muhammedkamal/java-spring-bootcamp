

import java.util.Scanner;
public class Lab6Calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter first number: ");
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid number");
            scanner.close();
            return;
        }
        double num1 = scanner.nextDouble();
        System.out.print("Enter second number: ");
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid number");
            scanner.close();
            return;
        }
        double num2 = scanner.nextDouble();
        int choice = -1;
        while (choice != 0) {
            System.out.println("Choose an operation:");
            System.out.println("1. Addition");
            System.out.println("2. Subtraction");
            System.out.println("3. Multiplication");
            System.out.println("4. Division");
            System.out.println("0. Exit");
            System.out.print("Your choice: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Unknown option");
                scanner.next();
                continue;
            }
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Result: " + (num1 + num2));
                    break;
                case 2:
                    System.out.println("Result: " + (num1 - num2));
                    break;
                case 3:
                    System.out.println("Result: " + (num1 * num2));
                    break;
                case 4:
                    if (num2 != 0) {
                        System.out.println("Result: " + (num1 / num2));
                    } else {
                        System.out.println("Cannot divide by zero");
                    }
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Unknown option");
            }
        }
        scanner.close();
    }
}