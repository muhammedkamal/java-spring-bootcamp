// break leaves the loop, continue skips to the next iteration.
public class BreakContinue {
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;        // skip even numbers
            }
            if (i > 7) {
                break;           // stop completely
            }
            System.out.print(i + " ");
        }
        System.out.println();    // 1 3 5 7
    }
}
