// Working with text.
public class StringsDemo {
    public static void main(String[] args) {
        String first = "Mona";
        String full = first + " Ali";

        System.out.println(full.length());            // 8
        System.out.println(full.toUpperCase());       // MONA ALI
        System.out.println(full.equals("Mona Ali"));  // true
        System.out.println("  spaced  ".trim());      // spaced
        System.out.println(full.contains("Ali"));     // true

        System.out.println("1" + 2 + 3);   // 123  (text first, so + joins)
        System.out.println(1 + 2 + "3");   // 33   (3 is computed first)
    }
}
