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

        // TODO: print every shape, then find and print the one with the largest area
        for (Shape shape: shapes){
            System.out.println(shape);
        }
    }
}

// TODO: abstract class Shape
abstract class Shape{
    private String name;
    Shape(String name){
        this.name = name;
    }
    abstract double area();
    abstract double perimeter();

    @Override
    public String toString() {
        return name + ": " + "area " + area() + ", " + "perimeter " + perimeter();
    }
}
// TODO: class Circle extends Shape
class Circle extends Shape {
    private double radius;
    Circle(double radius){
        super("Circle");
        this.radius = radius;
    }
    @Override
    double area() {
        return 3.14 *(radius*radius);
    }
    @Override
    double perimeter() {
        return (2*3.14)*radius;
    }
}
// TODO: class Rectangle extends Shape
class Rectangle extends Shape {

    private double width;
    private double height;
    Rectangle(double width,double height,String name){
        super(name);
        this.width = width;
        this.height = height;
    }Rectangle(double width,double height){
        super("Rectangle");
        this.width = width;
        this.height = height;
    }
    @Override
    double area() {
        return width * height;
    }
    @Override
    double perimeter() {
        return (width + height) * 2;
    }
}
// TODO: class Square extends Rectangle   (hint: a protected constructor in Rectangle that takes the name)
class Square extends Rectangle{
    private double side;
    Square(double side){
        super(side,side,"Square");
        this.side = side;
    }
    @Override
    double area() {
        return super.area();
    }
    @Override
    double perimeter() {
        return side * 4;
    }
}
// TODO: class Triangle extends Shape
class Triangle extends Shape{
    private double a;
    private double b;
    private double c;
    Triangle(double a, double b, double c){
        super("Triangle");
        this.a = a;
        this.b = b;
        this.c = c;
    }
    @Override
    double area() {
        double s = perimeter() / 2;
       return Math.sqrt(s*(s-a)*(s-b)*(s-c));
    }

    @Override
    double perimeter() {
        return a+b+c;
    }

}
