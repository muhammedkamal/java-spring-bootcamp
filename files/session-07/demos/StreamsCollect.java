import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// collect() turns a stream back into a collection: toList, joining, groupingBy, counting, summing.
public class StreamsCollect {
    record Student(String name, String city, int grade) {}

    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("Sara", "Cairo", 90),
                new Student("Omar", "Giza", 72),
                new Student("Mona", "Cairo", 85),
                new Student("Ali", "Giza", 60),
                new Student("Nour", "Alex", 95));

        System.out.println(students.stream().map(Student::name).collect(Collectors.joining(", ")));
        // Sara, Omar, Mona, Ali, Nour

        Map<String, List<Student>> byCity = students.stream().collect(Collectors.groupingBy(Student::city));
        System.out.println(new TreeMap<>(byCity).keySet());     // [Alex, Cairo, Giza]

        Map<String, Long> countPerCity = students.stream()
                .collect(Collectors.groupingBy(Student::city, TreeMap::new, Collectors.counting()));
        System.out.println(countPerCity);                       // {Alex=1, Cairo=2, Giza=2}

        Map<String, Double> averagePerCity = students.stream()
                .collect(Collectors.groupingBy(Student::city, TreeMap::new, Collectors.averagingInt(Student::grade)));
        System.out.println(averagePerCity);                     // {Alex=95.0, Cairo=87.5, Giza=66.0}

        Map<String, Integer> totalPerCity = students.stream()
                .collect(Collectors.groupingBy(Student::city, TreeMap::new, Collectors.summingInt(Student::grade)));
        System.out.println(totalPerCity);                       // {Alex=95, Cairo=175, Giza=132}

        Map<Boolean, List<String>> passFail = students.stream()
                .collect(Collectors.partitioningBy(s -> s.grade() >= 70,
                        Collectors.mapping(Student::name, Collectors.toList())));
        System.out.println(passFail);                           // {false=[Ali], true=[Sara, Omar, Mona, Nour]}

        Map<String, Integer> gradeByName = students.stream()
                .collect(Collectors.toMap(Student::name, Student::grade));
        System.out.println(gradeByName.get("Mona"));            // 85
    }
}
