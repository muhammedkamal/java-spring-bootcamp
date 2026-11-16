// Lab 1: Safe parsing and dividing with exceptions.
// parseOrDefault(text, fallback): Integer.parseInt on the trimmed text; return fallback for null or bad text.
// divide(a, b): parse both; if b is 0 throw new IllegalArgumentExceptio for the rest, return the sum.
// Check (main below):n("divisor is zero"); return a / b.
////   Do NOT catch NumberFormatException inside divide: let it reach main.
//// sumValid(items): add up the items that are numbers, print "Skipped N"
//   42
//   -1
//   -1
//   5
//   Bad input: divisor is zero
//   Bad number: For input string: "ten"
//   Skipped 2
//   12
public class Lab1SafeParse {
    public static void main(String[] args) {
        System.out.println(parseOrDefault("42", 0));
        System.out.println(parseOrDefault("4x2", -1));
        System.out.println(parseOrDefault(null, -1));

        String[][] cases = { { "10", "2" }, { "7", "0" }, { "ten", "2" } };
        for (String[] c : cases) {
            // TODO: call divide(c[0], c[1]) and print the result.
            try {
                System.out.println(divide(c[0],c[1]));
            // Catch NumberFormatException -> "Bad number: " + message
            }catch (NumberFormatException e){
                System.out.println("Bad number: " + e.getMessage());
            // Catch IllegalArgumentException -> "Bad input: " + message
            }catch (IllegalArgumentException e){
                System.out.println("Bad input: " + e.getMessage());
            }
            // Careful: NumberFormatException IS an IllegalArgumentException. Which catch goes first?
        }

        System.out.println(sumValid(new String[] { "5", "x", "10", "", "-3" }));
    }

    static int parseOrDefault(String text, int fallback) {
        // TODO
        if (text == null){
            return fallback;
        }
        try {
            return Integer.parseInt(text.trim());
        }catch (NumberFormatException e) {
            return fallback;
        }
    }

    static int divide(String a, String b) {
        // TODO
         int aInt = Integer.parseInt(a);
         int bInt = Integer.parseInt(b);
         if (bInt == 0){
             throw new IllegalArgumentException("Cant divide by 0");
         }
        return aInt / bInt;
    }

    static int sumValid(String[] items) {
        // TODO
        int sum = 0;
        int skipped = 0;
        for (String item : items){
            try {
                sum += Integer.parseInt(item);
            }catch (NumberFormatException e){
                skipped++;
                continue;
            }
        }
        System.out.printf("Skkiped %d%n",skipped);
        return sum;
    }
}
