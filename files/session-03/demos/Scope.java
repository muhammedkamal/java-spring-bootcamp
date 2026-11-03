// Scope: a variable lives only inside the braces where it is declared.
public class Scope {
    static void change(int n) {
        n = n + 100;                   // changes the COPY only
        System.out.println("inside: " + n);
    }

    public static void main(String[] args) {
        int n = 5;
        change(n);
        System.out.println("outside: " + n);   // still 5

        for (int i = 0; i < 3; i++) {
            int doubled = i * 2;       // exists only in this loop body
        }
        // System.out.println(i);      // does not compile: i is out of scope
    }
}
