import java.util.List;
import java.util.Map;
import java.util.Optional;

// Lab 1: Streams warm-up. Replace every loop with a stream pipeline.
// Check (main below):
//   evens squared: [4, 16, 36]
//   long words: [banana, cherry]
//   total length: 24
//   longest: banana
//   first starting with z: empty
//   joined: apple, banana, cherry, fig, kiwi
//   by length: {3=[fig], 4=[kiwi], 5=[apple], 6=[banana, cherry]}
import java.util.List;

import java.util.Map;

import java.util.Optional;

import java.util.Comparator;

import java.util.TreeMap;

import java.util.stream.Collectors;

public class Lab1StreamsWarmup {

    public static void main(String[] args) {

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

        List<String> words = List.of("apple", "banana", "cherry", "fig", "kiwi");

        System.out.println("evens squared: " + evensSquared(numbers));

        System.out.println("long words: " + longWords(words));

        System.out.println("total length: " + totalLength(words));

        System.out.println("longest: " + longest(words).orElse("none"));

        System.out.println("first starting with z: " + firstStartingWith(words, "z").map(w -> w).orElse("empty"));

        System.out.println("joined: " + joined(words));

        System.out.println("by length: " + byLength(words));

    }

    static List<Integer> evensSquared(List<Integer> numbers) {

        return numbers.stream()

                .filter(n -> n % 2 == 0)

                .map(n -> n * n)

                .toList();

    }

    static List<String> longWords(List<String> words) {

        return words.stream()

                .filter(word -> word.length() > 5)

                .toList();

    }

    static int totalLength(List<String> words) {

        return words.stream()

                .mapToInt(String::length)

                .sum();

    }

    static Optional<String> longest(List<String> words) {

        return words.stream()

                .max(Comparator.comparingInt(String::length));

    }

    static Optional<String> firstStartingWith(List<String> words, String prefix) {

        return words.stream()

                .filter(word -> word.startsWith(prefix))

                .findFirst();

    }

    static String joined(List<String> words) {

        return words.stream()

                .sorted()

                .collect(Collectors.joining(", "));

    }

    static Map<Integer, List<String>> byLength(List<String> words) {

        return words.stream()

                .collect(Collectors.groupingBy(

                        String::length,

                        TreeMap::new,

                        Collectors.toList()

                ));

    }

}
