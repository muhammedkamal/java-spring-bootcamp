// The four patterns you will use again and again: sum, max, search, frequency.
public class ArrayPatterns {
    static int sum(int[] a) {
        int total = 0;
        for (int v : a) total += v;
        return total;
    }

    static int max(int[] a) {
        int best = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] > best) best = a[i];
        }
        return best;
    }

    static int indexOf(int[] a, int target) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == target) return i;
        }
        return -1;                       // not found
    }

    static int count(int[] a, int target) {
        int c = 0;
        for (int v : a) {
            if (v == target) c++;
        }
        return c;
    }

    public static void main(String[] args) {
        int[] data = {4, 9, 2, 9, 7};
        System.out.println("sum   = " + sum(data));          // 31
        System.out.println("max   = " + max(data));          // 9
        System.out.println("index = " + indexOf(data, 7));   // 4
        System.out.println("nines = " + count(data, 9));     // 2
    }
}
