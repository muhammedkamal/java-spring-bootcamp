import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Comparable: the one natural order, written inside the class.
// Comparator: any other order, written outside the class.
public class Sorting {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>(List.of(
            new Student(3, "Sara", 88),
            new Student(1, "Omar", 92),
            new Student(2, "Ali", 88)
        ));

        Collections.sort(list);                     // uses compareTo: by id
        System.out.println(list);                   // [1 Omar 92, 2 Ali 88, 3 Sara 88]

        list.sort(new ByName());                    // a Comparator class
        System.out.println(list);                   // [2 Ali 88, 1 Omar 92, 3 Sara 88]

        // The same idea, shorter (lambdas and method references come next week)
        list.sort(Comparator.comparing(Student::mark).reversed()
                            .thenComparing(Student::name));
        System.out.println(list);                   // [1 Omar 92, 2 Ali 88, 3 Sara 88]
    }
}

class Student implements Comparable<Student> {
    private final int id;
    private final String name;
    private final int mark;

    Student(int id, String name, int mark) {
        this.id = id;
        this.name = name;
        this.mark = mark;
    }

    int id() { return id; }
    String name() { return name; }
    int mark() { return mark; }

    @Override
    public int compareTo(Student other) {           // negative: this first, positive: other first
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return id + " " + name + " " + mark;
    }
}

class ByName implements Comparator<Student> {
    @Override
    public int compare(Student a, Student b) {
        return a.name().compareTo(b.name());
    }
}
