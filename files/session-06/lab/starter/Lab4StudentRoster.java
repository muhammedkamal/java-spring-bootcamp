import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

// Lab 4: Remove duplicates, sort and group students with collections.
// 1. Two students are equal when their ids are equal. Write equals AND hashCode,
//    then a LinkedHashSet removes the duplicate Omar.
// 2. ByMarkThenName implements Comparator<Student>: highest mark first, equal marks by name.
// 3. byGrade(list): a TreeMap from grade ("A", "B", "C", "F") to the students with that grade.
// Check (main below):
//   6 -> 5
//   [Omar(92), Ali(88), Sara(88), Hany(74), Nour(55)]
//   {A=[Omar(92)], B=[Ali(88), Sara(88)], C=[Hany(74)], F=[Nour(55)]}
public class Lab4StudentRoster {
    public static void main(String[] args) {
        List<Student> raw = List.of(
            new Student(3, "Sara", 88),
            new Student(1, "Omar", 92),
            new Student(2, "Ali", 88),
            new Student(1, "Omar", 92),
            new Student(4, "Nour", 55),
            new Student(5, "Hany", 74)
        );

        Set<Student> unique = new LinkedHashSet<>(raw);
        System.out.println(raw.size() + " -> " + unique.size());

        List<Student> sorted = new ArrayList<>(unique);
        sorted.sort(new ByMarkThenName());
        System.out.println(sorted);

        System.out.println(byGrade(sorted));
    }

    static Map<String, List<Student>> byGrade(List<Student> students) {
        Map<String, List<Student>> groups = new TreeMap<>();
        // TODO
        return groups;
    }
}

class ByMarkThenName implements Comparator<Student> {
    @Override
    public int compare(Student a, Student b) {
        // TODO
        return 0;
    }
}

class Student {
    private final int id;
    private final String name;
    private final int mark;

    Student(int id, String name, int mark) {
        this.id = id;
        this.name = name;
        this.mark = mark;
    }

    String getName() {
        return name;
    }

    int getMark() {
        return mark;
    }

    String grade() {
        if (mark >= 90) return "A";
        if (mark >= 75) return "B";
        if (mark >= 60) return "C";
        return "F";
    }

    // TODO: equals and hashCode on id

    @Override
    public String toString() {
        return name + "(" + mark + ")";
    }
}
