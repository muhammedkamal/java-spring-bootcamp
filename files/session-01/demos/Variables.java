// A variable has a type, a name and a value.
public class Variables {
    public static void main(String[] args) {
        int age = 22;           // declare + assign
        double price = 49.99;
        String name = "Mona";

        System.out.println(name + " is " + age + ", ticket costs " + price);

        age = 23;               // change the value (the type stays int)
        System.out.println("Next year: " + age);

        // age = "twenty";      // compile error: incompatible types
    }
}
