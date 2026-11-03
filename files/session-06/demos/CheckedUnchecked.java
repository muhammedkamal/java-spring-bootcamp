import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

// Checked exceptions: the compiler forces you to catch them or declare them with throws.
// Unchecked exceptions (RuntimeException and its children): bugs; the compiler does not force anything.
public class CheckedUnchecked {
    public static void main(String[] args) {
        try {
            System.out.println(firstLine("hello\nworld"));     // hello
        } catch (IOException e) {
            System.out.println("Could not read: " + e.getMessage());
        }

        try {
            setAge(-5);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());                 // Age cannot be negative: -5
        }
    }

    // readLine() throws IOException (checked), so we must declare it or catch it
    static String firstLine(String text) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        return reader.readLine();
    }

    // IllegalArgumentException is unchecked: no throws needed
    static void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
        System.out.println("Age set to " + age);
    }
}
// throw  = throw one exception now (inside a method)
// throws = this method may throw these (in the signature)
