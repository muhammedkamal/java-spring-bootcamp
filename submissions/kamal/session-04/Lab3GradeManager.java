import java.util.Scanner;

// Lab 3 (Week 2 deliverable): Student Grade Manager.
// Menu: 1) Add  2) List  3) Average  4) Top  0) Exit
// Store students in an array of Student (size 10), keep a count of used slots.
// Reuse your Student class from Lab 2. Put it in this file below Lab3GradeManager.
public class Lab3GradeManager {
    static final int MAX = 10;
    static Student[] students = new Student[MAX];
    static int count = 0;

    // TODO: static void add(Scanner in)      -> reject a full class and marks outside 0-100
    static void add(Scanner in){
        if (count >= MAX){
            System.out.println("Full");
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
            System.out.println("Invalid mark! Must be between 0 and 100.");
            return;
        }
        students[count] = new Student(name, id, mark);
        count++;

        System.out.println("Student added successfully!");
    }
    // TODO: static void list()
    static void list(){
       if (count == 0){
           System.out.println("No students found");
            return;
       }
       for (int i = 0; i <count;i++){
           System.out.println(students[i]);
       }
    }
    // TODO: static double average()          -> 0 when there are no students
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
    // TODO: static Student top()             -> null when there are no students
    static Student top() {
        if (count == 0) {
            return null;
        }
        Student highest = students[0];

        for (int i = 1; i < count; i++) {
            if (students[i].getMark() > highest.getMark()) {
                highest = students[i];
            }
        }
        return highest;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean running = true;
        // TODO: menu loop as in Week 1 (running flag, switch on choice)
        // Tip: after nextInt() call in.nextLine() so the next nextLine() is not empty.
        while (running) {
            System.out.println("\n=== Student Grade Manager ===");
            System.out.printf("Menu: %n1) Add  %n2) List  %n3) Average  %n4) Top  %n0) Exit %n");
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
                    System.out.printf("Average: %.2f%n", average());
                    break;

                case 4:
                    Student best = top();
                    if (best == null) {
                        System.out.println("No students found.");
                    } else {
                        System.out.println("Top student: " + best);
                    }
                    break;

                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        in.close();
    }
}