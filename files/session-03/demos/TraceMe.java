// Trace by hand first, then run. Fill the table on paper.
public class TraceMe {
    public static void main(String[] args) {
        int x = 3;
        int total = 0;
        for (int i = 1; i <= x; i++) {
            if (i % 2 == 1) {
                total += i * 10;
            } else {
                total -= i;
            }
        }
        System.out.println(total);   // answer: 38
    }
}
