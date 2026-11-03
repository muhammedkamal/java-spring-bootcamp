// One-dimensional arrays: fixed size, zero-based index.
import java.util.Arrays;

public class Arrays1D {
    public static void main(String[] args) {
        int[] scores = {70, 85, 90, 60, 95};
        System.out.println(scores.length);          // 5
        System.out.println(scores[0]);              // 70
        scores[1] = 88;

        for (int i = 0; i < scores.length; i++) {
            System.out.print(scores[i] + " ");
        }
        System.out.println();

        for (int s : scores) {                      // enhanced for
            System.out.print(s + " ");
        }
        System.out.println();

        System.out.println(Arrays.toString(scores));
        // scores[5] = 1;   // ArrayIndexOutOfBoundsException
    }
}
