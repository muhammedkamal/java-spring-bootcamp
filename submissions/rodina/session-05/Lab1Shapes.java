// Lab 1: Shape hierarchy.
// abstract class Shape: a name, abstract area() and perimeter(), and a toString()
//   that prints "Circle: area 7.07, perimeter 9.42" (two decimals).
// Subclasses: Circle(radius), Rectangle(width, height), Square(side) extends Rectangle,
//   Triangle(a, b, c) with Heron's formula: s = perimeter / 2, area = sqrt(s(s-a)(s-b)(s-c)).
// Check (main below):
//   Circle: area 7.07, perimeter 9.42
//   Rectangle: area 10.00, perimeter 13.00
//   Square: area 9.00, perimeter 12.00
//   Triangle: area 6.00, perimeter 12.00
//   Largest: Rectangle
public class Lab1Shapes {
    public static void main(String[] args) {
        Shape[] shapes = {
                new Circle(1.5),
                new Rectangle(4, 2.5),
                new Square(3),
                new Triangle(3, 4, 5)
        };

        Shape largest = shapes[0];
        for (Shape s : shapes) {
            System.out.println(s);
            if (s.area() > largest.area()) {
                largest = s;
            }
        }
        System.out.println("Largest: " + largest.getName());
    }
}

abstract class Shape {
    private final String name;

    Shape(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    abstract double area();

    abstract double perimeter();

    @Override
    public String toString() {
        return String.format("%s: area %.2f, perimeter %.2f", name, area(), perimeter());
    }
}
class Circle extends Shape {
    private final double radius;
    Circle (double radius){
        super("Circle");
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }

    @Override
    double perimeter() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        this("Rectangle", width, height);
    }

    protected Rectangle(String name, double width, double height) {
        super(name);
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }

    @Override
    double perimeter() {
        return 2 * (width + height);
    }
}

class Square extends Rectangle {
    Square(double side) {
        super("Square", side, side);
    }
}

class Triangle extends Shape {
    private final double a;
    private final double b;
    private final double c;

    Triangle(double a, double b, double c) {
        super("Triangle");
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    double area() {
        double s = perimeter() / 2;                 // Heron's formula
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    double perimeter() {
        return a + b + c;
    }

}

