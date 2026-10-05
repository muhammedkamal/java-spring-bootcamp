// The types used every day.
public class Types {
    public static void main(String[] args) {
        int students = 30;
        long views = 3_000_000_000L;   // too big for int, needs L
        double salary = 15000.50;
        boolean isActive = true;
        char grade = 'A';              // single quotes
        String city = "Cairo";         // double quotes, a class

        System.out.println("students = " + students);
        System.out.println("views    = " + views);
        System.out.println("salary   = " + salary);
        System.out.println("isActive = " + isActive);
        System.out.println("grade    = " + grade);
        System.out.println("city     = " + city);

        System.out.println("int max  = " + Integer.MAX_VALUE);
    }
}
