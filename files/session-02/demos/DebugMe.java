// Debugger drill: set a breakpoint on the for line, step over (F8), watch sum and i.
// Expected: sum of 1..5 = 15. What does it print? Find the bug.
public class DebugMe {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 1; i < 5; i++) {
            sum += i;
        }
        System.out.println("Sum 1..5 = " + sum);
    }
}
