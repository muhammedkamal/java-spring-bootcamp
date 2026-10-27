// Lab 1: Safe parsing and dividing with exceptions.
// parseOrDefault(text, fallback): Integer.parseInt on the trimmed text; return fallback for null or bad text.
// divide(a, b): parse both; if b is 0 throw new IllegalArgumentException("divisor is zero"); return a / b.
//   Do NOT catch NumberFormatException inside divide: let it reach main.

public class Lab1SafeParse {
    public static void main(String[] args) {
        System.out.println(parseOrDefault("42", 0));
        System.out.println(parseOrDefault("4x2", -1));
        System.out.println(parseOrDefault(null, -1));
        String[][] cases = {
                {"10", "2"},
                {"7", "0"},
                {"ten", "2"}
        };
        for (String[] c : cases) {
            try {
                System.out.println(divide(c[0], c[1]));
            } catch (NumberFormatException e) {
                System.out.println("Bad number: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Bad input: " + e.getMessage());
            }
        }
        System.out.println(sumValid(
                new String[] {"5", "x", "10", "", "-3"}
        ));
    }
    static int parseOrDefault(String text, int fallback) {
        if (text == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
    static int divide(String a, String b) {
        int num1 = Integer.parseInt(a.trim());
        int num2 = Integer.parseInt(b.trim());
        if (num2 == 0) {
            throw new IllegalArgumentException("divisor is zero");
        }
        return num1 / num2;
    }
    static int sumValid(String[] items) {
        int sum = 0;
        int skipped = 0;
        for (String item : items) {
            try {
                sum += Integer.parseInt(item.trim());
            } catch (NumberFormatException e) {
                skipped++;
            }
        }
        System.out.println("Skipped " + skipped);
        return sum;
    }
}