import java.util.List;

// A stream is a pipeline: source -> intermediate steps (lazy) -> terminal step (runs it).
public class StreamsBasics {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(5, 12, 8, 3, 20, 7, 12);

        List<Integer> bigSquares = numbers.stream()
                .filter(n -> n >= 8)            // keep
                .map(n -> n * n)                // transform
                .sorted()                       // order
                .distinct()                     // remove duplicates
                .toList();                      // terminal: collect
        System.out.println(bigSquares);         // [64, 144, 400]

        System.out.println(numbers.stream().count());                           // 7
        System.out.println(numbers.stream().mapToInt(Integer::intValue).sum()); // 67
        System.out.println(numbers.stream().mapToInt(n -> n).average().getAsDouble()); // 9.571428571428571
        System.out.println(numbers.stream().anyMatch(n -> n > 15));             // true
        System.out.println(numbers.stream().allMatch(n -> n > 5));              // false
        System.out.println(numbers.stream().max(Integer::compare).get());       // 20
        System.out.println(numbers.stream().limit(3).toList());                 // [5, 12, 8]
        System.out.println(numbers.stream().skip(5).toList());                  // [7, 12]

        // Lazy: nothing runs until the terminal step
        var pipeline = numbers.stream().filter(n -> {
            System.out.println("checking " + n);
            return n > 10;
        });
        System.out.println("pipeline built, nothing checked yet");
        System.out.println(pipeline.toList());
        // pipeline built, nothing checked yet
        // checking 5 ... checking 12 ... (all 7), then [12, 20, 12]

        // A stream can be used once
        var s = numbers.stream();
        s.count();
        try {
            s.count();
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());    // stream has already been operated upon or closed
        }
    }
}
