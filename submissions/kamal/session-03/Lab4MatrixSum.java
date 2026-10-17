import java.util.Arrays;

// Lab 4: Sum of all values in a 2D array, and the sum of each row.
// Check: {{1,2,3},{4,5,6},{7,8,9}} -> total 45, rows 6, 15, 24
public class Lab4MatrixSum {
    // TODO: static int sumAll(int[][] m)
    // TODO: static int[] rowSums(int[][] m)

    public static void main(String[] args) {
        int[][] m = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        // TODO: print the total and each row sum
        System.out.println(sumAll(m));
        System.out.println(Arrays.toString(rowSums(m)));
    }
    public static int sumAll(int[][] m){
        int sum = 0;
        for (int i = 0; i <= m.length-1; i++){
            for (int j = 0 ;j <= m.length-1; j++){
                sum += m[i][j];
            }
        }
        return sum;
    }
    public static int[] rowSums(int[][] m){
        int[] sum = new int[m.length] ;
        for (int i =0; i < m.length; i++){
            int rowSum=0;
            for (int j = 0 ;j < m.length; j++){
                rowSum += m[i][j];
            }
            sum[i] = rowSum;
        }
        return sum;
    }
}
