// Count the work, not the seconds.
public class BigODemo {
    // O(n): at most n comparisons
    static int linearSearch(int[] a, int target) {
        int steps = 0;
        for (int v : a) {
            steps++;
            if (v == target) break;
        }
        return steps;
    }

    // O(n^2): n rounds of n comparisons
    static int pairCount(int n) {
        int steps = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                steps++;
            }
        }
        return steps;
    }

    public static void main(String[] args) {
        int[] a = new int[1000];
        for (int i = 0; i < a.length; i++) a[i] = i;
        System.out.println("linear, last item : " + linearSearch(a, 999));   // 1000
        System.out.println("pairs, n = 10     : " + pairCount(10));         // 100
        System.out.println("pairs, n = 100    : " + pairCount(100));        // 10000
    }
}
