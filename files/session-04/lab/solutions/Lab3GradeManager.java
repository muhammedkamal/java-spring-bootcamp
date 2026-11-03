import java.util.Objects;
import java.util.Scanner;

// Week 2 deliverable: Student Grade Manager.
public class Lab3GradeManager {
    static final int MAX = 10;
    static Student[] students = new Student[MAX];
    static int count = 0;

    static void add(Scanner in) {
        if (count == MAX) {
            System.out.println("Class is full");
            return;
        }
        System.out.print("Name: ");
        String name = in.nextLine().trim();
        System.out.print("Mark (0-100): ");
        int mark = in.nextInt();
        in.nextLine();
        if (mark < 0 || mark > 100) {
            System.out.println("Invalid mark");
            return;
        }
        students[count++] = new Student(name, 1000 + count, mark);
    }

    static void list() {
        if (count == 0) {
            System.out.println("No students yet");
        }
        for (int i = 0; i < count; i++) {
            System.out.println(students[i]);
        }
    }

    static double average() {
        if (count == 0) return 0;
        double sum = 0;
        for (int i = 0; i < count; i++) {
            sum += students[i].getMark();
        }
        return sum / count;
    }

    static Student top() {
        if (count == 0) return null;
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
            System.out.println("1) Add  2) List  3) Average  4) Top  0) Exit");
            System.out.print("Choice: ");
            int choice = in.nextInt();
            in.nextLine();
            switch (choice) {
                case 1 -> add(in);
                case 2 -> list();
                case 3 -> System.out.printf("Average: %.2f%n", average());
                case 4 -> {
                    Student t = top();
                    System.out.println(t == null ? "No students yet" : "Top: " + t);
                }
                case 0 -> running = false;
                default -> System.out.println("Unknown option");
            }
        }
        in.close();
    }
}

class Student {
    private final String name;
    private final int id;
    private int mark;

    Student(String name, int id, int mark) {
        this.name = name;
        this.id = id;
        setMark(mark);
    }

    void setMark(int mark) {
        this.mark = Math.min(100, Math.max(0, mark));
    }

    int getMark() {
        return mark;
    }

    String getName() {
        return name;
    }

    String grade() {
        if (mark >= 90) return "A";
        if (mark >= 75) return "B";
        if (mark >= 60) return "C";
        return "Fail";
    }

    @Override
    public String toString() {
        return id + " " + name + ": " + mark + " (" + grade() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
