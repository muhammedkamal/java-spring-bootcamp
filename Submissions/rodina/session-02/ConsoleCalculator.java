

// Lab 6 (Week 1 deliverable): console calculator with a menu loop.
// Menu: 1) Add  2) Subtract  3) Multiply  4) Divide  0) Exit
// After each operation, show the menu again until the user picks 0.
// Rules: division by zero prints "Cannot divide by zero"; unknown choice prints "Unknown option".
// Hint: see demos/FloorMenu.java for the loop shape.
import java.util.Scanner;

public class ConsoleCalculator {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        int choice;

        do {

            System.out.println("\n=== Calculator Menu ===");

            System.out.println("1. +");

            System.out.println("2. -");

            System.out.println("3. *");

            System.out.println("4. /");

            System.out.println("5. Exit");

            System.out.print("Choose an operation: ");

            choice = input.nextInt();

            if (choice >= 1 && choice <= 4) {

                System.out.print("Enter first number: ");

                double num1 = input.nextDouble();

                System.out.print("Enter second number: ");

                double num2 = input.nextDouble();

                switch (choice) {

                    case 1:

                        System.out.println("Result = " + (num1 + num2));

                        break;

                    case 2:

                        System.out.println("Result = " + (num1 - num2));

                        break;

                    case 3:

                        System.out.println("Result = " + (num1 * num2));

                        break;

                    case 4:

                        if (num2 == 0) {

                            System.out.println("Cannot divide by zero");

                        } else {

                            System.out.println("Result = " + (num1 / num2));

                        }

                        break;

                }

            } else if (choice != 5) {

                System.out.println("Invalid choice");

            }

        } while (choice != 5);

        System.out.println("Calculator closed.");

        input.close();

    }

}

