import java.util.Scanner;

// Lab 3 (Week 2 deliverable): Student Grade Manager.
// Menu: 1) Add  2) List  3) Average  4) Top  0) Exit
// Store students in an array of Student (size 10), keep a count of used slots.
// Reuse your Student class from Lab 2. Put it in this file below Lab3GradeManager.
import java.util.Scanner;
// Lab 3: Student Grade Manager
import java.util.Scanner;
public class Lab3GradeManager {
    static final int MAX = 10;
    static Student[] students = new Student[MAX];
    static int count = 0;
    static void add(Scanner in) {
        if (count >= MAX) {
            System.out.println("Class is full.");
            return;
        }
        System.out.print("Enter student name: ");
        String name = in.nextLine();
        System.out.print("Enter student ID: ");
        int id = in.nextInt();
        System.out.print("Enter student mark: ");
        int mark = in.nextInt();
        in.nextLine();
        if (mark < 0 || mark > 100) {
            System.out.println("Invalid mark. Mark must be between 0 and 100.");
            return;
        }
        students[count] = new Student(name, id, mark);
        count++;
        System.out.println("Student added successfully.");
    }
    static void list() {
        if (count == 0) {
            System.out.println("No students.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println(students[i]);
        }
    }
    static double average() {
        if (count == 0) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += students[i].getMark();
        }
        return (double) sum / count;
    }
    static Student top() {
        if (count == 0) {
            return null;
        }
        Student best = students[0];
        for (int i = 1; i < count; i++) {
            if (students[i].getMark() > best.getMark()) {
                best = students[i];
            }
        }
        return best;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n--- Student Grade Manager ---");
            System.out.println("1) Add");
            System.out.println("2) List");
            System.out.println("3) Average");
            System.out.println("4) Top");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            int choice = in.nextInt();
            in.nextLine();
            switch (choice) {
                case 1:
                    add(in);
                    break;
                case 2:
                    list();
                    break;
                case 3:
                    System.out.println("Average: " + average());
                    break;
                case 4:
                    Student best = top();
                    if (best == null) {
                        System.out.println("No students.");
                    } else {
                        System.out.println("Top student: " + best);
                    }
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        in.close();
    }
}