// Strings never change. Build text in a loop with StringBuilder.
public class StringBuilderDemo {
    public static void main(String[] args) {
        // Slow way: creates a new String every round
        String s = "";
        for (int i = 1; i <= 5; i++) {
            s = s + i + ",";
        }
        System.out.println(s);

        // Right way: one buffer, change it in place
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i).append(",");
        }
        sb.setLength(sb.length() - 1);               // drop the last comma
        System.out.println(sb);                      // 1,2,3,4,5

        System.out.println(new StringBuilder("stressed").reverse());   // desserts
    }
}
