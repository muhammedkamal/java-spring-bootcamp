public class Lab4MatrixSum {
    static int sumAll(int[][] m) {
        int total = 0;
        for (int[] row : m) {
            for (int v : row) {
                total += v;
            }
        }
        return total;
    }

    static int[] rowSums(int[][] m) {
        int[] sums = new int[m.length];
        for (int r = 0; r < m.length; r++) {
            for (int v : m[r]) {
                sums[r] += v;
            }
        }
        return sums;
    }

    public static void main(String[] args) {
        int[][] m = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("Total: " + sumAll(m));
        int[] sums = rowSums(m);
        for (int r = 0; r < sums.length; r++) {
            System.out.println("Row " + r + ": " + sums[r]);
        }
    }
}
