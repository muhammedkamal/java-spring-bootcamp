import java.util.Scanner;

// Lab 6 (Week 1 deliverable): console calculator with a menu loop.
// Menu: 1) Add  2) Subtract  3) Multiply  4) Divide  0) Exit
// After each operation, show the menu again until the user picks 0.
// Rules: division by zero prints "Cannot divide by zero"; unknown choice prints "Unknown option".
// Hint: see demos/FloorMenu.java for the loop shape.
public class Lab6Calculator {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: while loop with a running flag
        boolean flag = true;
        while (flag){
        // TODO: print the menu, read choice
            System.out.printf("Menu:  %n1) Add  %n2) Subtract  %n3) Multiply  %n4) Divide  %n0) Exit%n");
            System.out.print("Write number of operation: ");
            if (!in.hasNextInt()) {
                System.out.print("Invalid input! Please enter a number.");
                in.next();
                continue;
            }

            int operation = in.nextInt();
            if (operation == 0) {
                flag = false;
                break;
            }
            if (operation < 1 || operation > 4) {
                System.out.println("Unknown option");
                continue;
            }
        // TODO: for 1-4 read two doubles and print the result (use switch)
            System.out.print("Write number 1: ");
            double num1 = in.nextDouble();

            System.out.print("Write number 2: ");
            double num2 = in.nextDouble();

            switch (operation){
                case 1 -> System.out.printf("Result = %.2f%n", num1+num2);
                case 2 -> System.out.printf("Result = %.2f%n", num1 - num2);
                case 3 -> System.out.printf("Result = %.2f%n", num1*num2);
                case 4 -> {
                    if (num2 == 0) {
                        System.out.println("Cannot divide by zero");
                    } else {
                        System.out.printf("Result = %.2f%n", num1 / num2);
                    }
                }

            }
        }
        in.close();
    }
}
