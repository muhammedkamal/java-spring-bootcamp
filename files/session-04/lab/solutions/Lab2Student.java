import java.util.Objects;

public class Lab2Student {
    public static void main(String[] args) {
        Student s1 = new Student("Mona", 1001, 88);
        Student s2 = new Student("Mona", 1001, 70);
        System.out.println(s1);                         // 1001 Mona: 88 (B)
        System.out.println(s1.equals(s2));              // true, same id
        System.out.println(new Student("Omar", 1002, 140).getMark());   // 100 (clamped)
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
