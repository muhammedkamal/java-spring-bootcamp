// Two-dimensional arrays: rows of columns.
public class Matrix2D {
    public static void main(String[] args) {
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println(grid.length);        // 3 rows
        System.out.println(grid[0].length);     // 3 columns
        System.out.println(grid[1][2]);         // 6

        int total = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                total += grid[r][c];
            }
        }
        System.out.println("total = " + total); // 45
    }
}
