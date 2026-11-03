// Lab 4: Sum of all values in a 2D array, and the sum of each row.
// Check: {{1,2,3},{4,5,6},{7,8,9}} -> total 45, rows 6, 15, 24
public class Lab4MatrixSum {
     static int sumAll(int[][] m){
         int total = 0;
         for (int i =0;i<m.length;i++){
             for (int j=0;j<m[i].length;j++){
                 total+=m[i][j];
             }
         }
         return total;
     }
     static int[] rowSums(int[][] m){
         int[] sums = new int[m.length];
         for (int i =0;i<m.length;i++) {
             int sum = 0;
             for (int j =0;j <m[i].length;j++){
                 sum += m[i][j];
             }
             sums[i]=sum;
         }
         return sums;
     }

    public static void main(String[] args) {
        int[][] m = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("Total sum: " + sumAll(m));
        int[] sums =rowSums(m);
        for (int i =0;i<sums.length;i++){
            System.out.println("Row"+(i+1)+"sum:"+sums[i]);
        }
        // TODO: print the total and each row sum
    }
}
