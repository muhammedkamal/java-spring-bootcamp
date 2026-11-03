import java.util.ArrayList;
import java.util.List;

// Lab 5: Generic Pair, generic methods and a generic stack.
// Pair<A, B>: first, second, getters, toString() -> "(age, 30)".
// swap(pair): returns a new Pair with first and second swapped.
// minMax(list): <T extends Comparable<T>>, returns Pair(min, max); empty list -> IllegalArgumentException.
// SimpleStack<T>: push, pop, peek, isEmpty, size, backed by an ArrayList.
//   pop and peek on an empty stack throw IllegalStateException("stack is empty").
// Check (main below):
//   (age, 30)
//   (30, age)
//   (2, 9)
//   (apple, pear)
//   cb left 1
//   a
//   true
//   stack is empty
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

    // TODO: static <A, B> Pair<B, A> swap(Pair<A, B> pair)
    // TODO: static <T extends Comparable<T>> Pair<T, T> minMax(List<T> items)
}

// TODO: class Pair<A, B>
// TODO: class SimpleStack<T>
