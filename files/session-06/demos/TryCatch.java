// try / catch / finally: handle a problem instead of crashing.
public class TryCatch {
    public static void main(String[] args) {
        String[] inputs = { "42", "abc", "0" };

        for (String text : inputs) {
            try {
                int n = Integer.parseInt(text);     // may throw NumberFormatException
                System.out.println(100 / n);        // may throw ArithmeticException
            } catch (NumberFormatException e) {
                System.out.println("Not a number: " + text);
            } catch (ArithmeticException e) {
                System.out.println("Cannot divide by zero");
            } finally {
                System.out.println("  (checked " + text + ")");    // always runs
            }
        }
        // 2, (checked 42), Not a number: abc, (checked abc), Cannot divide by zero, (checked 0)

        int[] a = new int[3];
        try {
            a[3] = 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {     // multi-catch
            System.out.println("Caught: " + e.getMessage());
        }
        // Caught: Index 3 out of bounds for length 3

        System.out.println("Program keeps going");
    }
}
