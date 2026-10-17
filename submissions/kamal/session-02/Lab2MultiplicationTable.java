import java.util.Scanner;

// Lab 2: Read n and print its multiplication table from 1 to 10.
// Check: n = 7 -> 7 x 1 = 7 ... 7 x 10 = 70
public class Lab2MultiplicationTable {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read n
        System.out.print("Enter Number:  ");
        int n = in.nextInt();
        // TODO: loop 1..10 and print "n x i = result"
        for (int x = 1; x <= 10; x++){
            System.out.printf("%d × %d = %d, ",n,x,n*x);
        }
        in.close();
    }
}
