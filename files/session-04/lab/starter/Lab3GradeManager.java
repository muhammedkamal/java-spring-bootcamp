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
    // TODO: static void list()
    // TODO: static double average()          -> 0 when there are no students
    // TODO: static Student top()             -> null when there are no students

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: menu loop as in Week 1 (running flag, switch on choice)
        // Tip: after nextInt() call in.nextLine() so the next nextLine() is not empty.
        in.close();
    }
}

// TODO: paste class Student here
