public class Lab7SecondLargest {
    // Returns Integer.MIN_VALUE if the array has fewer than two distinct values.
    static int secondLargest(int[] a) {
        int first = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        for (int v : a) {
            if (v > first) {
                second = first;
                first = v;
            } else if (v > second && v < first) {
                second = v;
            }
        }
        return second;
    }

    public static void main(String[] args) {
        System.out.println(secondLargest(new int[]{4, 9, 2, 9, 7}));   // 7
        System.out.println(secondLargest(new int[]{5, 5, 5}));         // -2147483648
        System.out.println(secondLargest(new int[]{-3, -8, -1}));      // -3
    }
}
