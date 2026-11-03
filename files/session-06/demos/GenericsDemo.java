import java.util.List;

// Generics: write a class or method once, use it with any type, and let the compiler check it.
public class GenericsDemo {
    public static void main(String[] args) {
        Box<String> name = new Box<>("Mona");
        Box<Integer> age = new Box<>(25);
        String n = name.get();                      // no cast needed
        // name.set(42);                            // does not compile: Box<String> holds Strings
        System.out.println(n + " " + age.get());    // Mona 25

        Pair<String, Integer> entry = new Pair<>("pen", 12);
        System.out.println(entry);                  // (pen, 12)

        System.out.println(firstOrDefault(List.of("a", "b"), "none"));   // a
        System.out.println(firstOrDefault(List.<String>of(), "none"));   // none

        System.out.println(max(List.of(3, 9, 4)));               // 9
        System.out.println(max(List.of("kiwi", "apple", "fig"))); // kiwi
    }

    // A generic method: <T> before the return type
    static <T> T firstOrDefault(List<T> list, T fallback) {
        return list.isEmpty() ? fallback : list.get(0);
    }

    // Bounded: T must know how to compare itself
    static <T extends Comparable<T>> T max(List<T> list) {
        T best = list.get(0);
        for (T item : list) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }
}

class Box<T> {
    private T value;

    Box(T value) {
        this.value = value;
    }

    T get() {
        return value;
    }

    void set(T value) {
        this.value = value;
    }
}

class Pair<K, V> {
    private final K key;
    private final V value;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getKey() { return key; }
    V getValue() { return value; }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
