// Modern switch and instanceof: test the type and unpack it in one step.
public class PatternMatching {
    sealed interface Shape permits Circle, Rectangle, Square {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape shape) {
        return switch (shape) {                         // no default needed: Shape is sealed
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Square s -> s.side() * s.side();
        };
    }

    static String describe(Object o) {
        return switch (o) {
            case null -> "null";
            case Integer i when i > 100 -> "big int " + i;
            case Integer i -> "int " + i;
            case String s -> "string of length " + s.length();
            case Rectangle(double w, double h) -> "rectangle " + w + " x " + h;    // record pattern
            default -> "something else";
        };
    }

    public static void main(String[] args) {
        Object o = "hello";
        if (o instanceof String s && s.length() > 3) {  // pattern matching for instanceof
            System.out.println(s.toUpperCase());        // HELLO
        }

        System.out.println(Math.round(area(new Circle(1)) * 100) / 100.0);      // 3.14
        System.out.println(area(new Rectangle(2, 3)));  // 6.0
        System.out.println(area(new Square(4)));        // 16.0

        System.out.println(describe(7));                // int 7
        System.out.println(describe(700));              // big int 700
        System.out.println(describe("java"));           // string of length 4
        System.out.println(describe(new Rectangle(2, 5)));      // rectangle 2.0 x 5.0
        System.out.println(describe(3.5));              // something else
        System.out.println(describe(null));             // null

        // Switch expressions on plain values
        int day = 3;
        String name = switch (day) {
            case 1, 7 -> "weekend";
            case 2, 3, 4, 5, 6 -> "weekday";
            default -> "invalid";
        };
        System.out.println(name);                       // weekday

        // Text blocks
        String json = """
                {
                  "title": "Dune",
                  "year": 1965
                }
                """;
        System.out.print(json);
    }
}
