// Lab 7: Second-largest value of an int array, in a single pass.
// Check: {4,9,2,9,7} -> 7, {-3,-8,-1} -> -3
// Rule: if there is no second distinct value, return Integer.MIN_VALUE.
public class Lab7SecondLargest {
    static int secondLargest(int[] a) {
        int largest = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > largest) {
                second = largest;
                largest = a[i];
            } else if (a[i] > second && a[i] < largest) {
                second = a[i];

            }

        }
        return second;
    }

    public static void main(String[] args) {
        int[] a1 = {4, 9, 2, 9, 7};
        int[] a2 = {-3, -8, -1};
        int[] a3 = {5, 5, 5};
        System.out.println(secondLargest(a1));
        System.out.println(secondLargest(a2));
        System.out.println(secondLargest(a3));
    }
}