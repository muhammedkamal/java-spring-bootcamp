// Lab 7: Second-largest value of an int array, in a single pass.
// Check: {4,9,2,9,7} -> 7, {-3,-8,-1} -> -3
// Rule: if there is no second distinct value, return Integer.MIN_VALUE.
public class Lab7SecondLargest {
    // TODO: static int secondLargest(int[] a)

    public static void main(String[] args) {
        // TODO: call secondLargest on the three check arrays and print the results
        int[] arr1 = {4, 9, 2, 9, 7};
        int[] arr2 = {-3, -8, -1};
        int[] arr3 = {5, 5, 5};

        System.out.println(secondLargest(arr1));
        System.out.println(secondLargest(arr2));
        System.out.println(secondLargest(arr3));
    }
    public static int secondLargest(int[] a){
        int largest = Integer.MIN_VALUE;
        int secondLargest = Integer.MIN_VALUE;

        for (int i = 0; i < a.length; i++) {
            if (a[i] > largest) {
                secondLargest = largest;
                largest = a[i];
            } else if (a[i] < largest && a[i] > secondLargest) {
                secondLargest = a[i];
            }
        }
        return secondLargest;
    }
}
