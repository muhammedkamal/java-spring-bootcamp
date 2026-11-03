import java.util.ArrayList;
import java.util.List;

// Lab 5: Generic Pair, generic methods and a generic stack. Reference solution.
public class Lab5Generics {
    public static void main(String[] args) {
        Pair<String, Integer> p = new Pair<>("age", 30);
        System.out.println(p);
        System.out.println(swap(p));

        System.out.println(minMax(List.of(7, 2, 9, 4)));
        System.out.println(minMax(List.of("pear", "apple", "fig")));

        SimpleStack<String> stack = new SimpleStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        System.out.println(stack.pop() + stack.pop() + " left " + stack.size());
        System.out.println(stack.peek());
        stack.pop();
        System.out.println(stack.isEmpty());
        try {
            stack.pop();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    static <A, B> Pair<B, A> swap(Pair<A, B> pair) {
        return new Pair<>(pair.getSecond(), pair.getFirst());
    }

    static <T extends Comparable<T>> Pair<T, T> minMax(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("empty list");
        }
        T min = items.get(0);
        T max = items.get(0);
        for (T item : items) {
            if (item.compareTo(min) < 0) min = item;
            if (item.compareTo(max) > 0) max = item;
        }
        return new Pair<>(min, max);
    }
}

class Pair<A, B> {
    private final A first;
    private final B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    A getFirst() {
        return first;
    }

    B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

class SimpleStack<T> {
    private final List<T> items = new ArrayList<>();

    void push(T item) {
        items.add(item);
    }

    T pop() {
        if (items.isEmpty()) {
            throw new IllegalStateException("stack is empty");
        }
        return items.remove(items.size() - 1);
    }

    T peek() {
        if (items.isEmpty()) {
            throw new IllegalStateException("stack is empty");
        }
        return items.get(items.size() - 1);
    }

    boolean isEmpty() {
        return items.isEmpty();
    }

    int size() {
        return items.size();
    }
}
