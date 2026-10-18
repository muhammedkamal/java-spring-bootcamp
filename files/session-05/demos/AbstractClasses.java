// abstract: a class that is only a template. It can have abstract methods with no body.
public class AbstractClasses {
    public static void main(String[] args) {
        Shape[] shapes = { new Circle(1), new Rectangle(2, 3), new Circle(2) };

        double total = 0;
        for (Shape s : shapes) {
            System.out.println(s.describe());
            total += s.area();
        }
        System.out.printf("Total area: %.2f%n", total);   // Total area: 21.71

        // Shape s = new Shape("x");            // does not compile: Shape is abstract
    }
}

abstract class Shape {
    private final String name;

    Shape(String name) {
        this.name = name;
    }

    abstract double area();                     // every subclass MUST write this

    String describe() {                         // normal method, shared by all
        return String.format("%s with area %.2f", name, area());
    }
}

class Circle extends Shape {
    private final double radius;

    Circle(double radius) {
        super("Circle");
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        super("Rectangle");
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }
}
