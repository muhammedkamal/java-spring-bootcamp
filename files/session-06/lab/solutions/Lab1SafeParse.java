// Lab 1: Safe parsing and dividing with exceptions. Reference solution.
public class Lab1SafeParse {
    public static void main(String[] args) {
        System.out.println(parseOrDefault("42", 0));
        System.out.println(parseOrDefault("4x2", -1));
        System.out.println(parseOrDefault(null, -1));

        String[][] cases = { { "10", "2" }, { "7", "0" }, { "ten", "2" } };
        for (String[] c : cases) {
            try {
                System.out.println(divide(c[0], c[1]));
            } catch (NumberFormatException e) {
                System.out.println("Bad number: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Bad input: " + e.getMessage());
            }
        }

        System.out.println(sumValid(new String[] { "5", "x", "10", "", "-3" }));
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
        int x = Integer.parseInt(a);
        int y = Integer.parseInt(b);
        if (y == 0) {
            throw new IllegalArgumentException("divisor is zero");
        }
        return x / y;
    }

    static int sumValid(String[] items) {
        int sum = 0;
        int skipped = 0;
        for (String item : items) {
            try {
                sum += Integer.parseInt(item);
            } catch (NumberFormatException e) {
                skipped++;
            }
        }
        System.out.println("Skipped " + skipped);
        return sum;
    }
}
