

public class Lab2Student {
    public static void main(String[] args) {
        Student s1 = new Student("Mona", 1001, 88);
        Student s2 = new Student("Mona", 1001, 70);
        System.out.println(s1);
        System.out.println(s1.equals(s2));
        System.out.println(new Student("Omar", 1002, 140).getMark());
    }
}
class Student {
    private String name;
    private int id;
    private int mark;
    public Student(String name, int id, int mark) {
        this.name = name;
        this.id = id;
        setMark(mark);
    }
    public void setMark(int mark) {
        if (mark < 0) {
            this.mark = 0;
        } else if (mark > 100) {
            this.mark = 100;
        } else {
            this.mark = mark;
        }
    }
    public int getMark() {
        return mark;
    }
    public String getName() {
        return name;
    }
    public String grade() {
        if (mark >= 90) {
            return "A";
        } else if (mark >= 75) {
            return "B";
        } else if (mark >= 60) {
            return "C";
        } else {
            return "Fail";
        }
    }
    @Override
    public String toString() {
        return id + " " + name + ": " + mark + " (" + grade() + ")";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        return this.id == other.id;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}