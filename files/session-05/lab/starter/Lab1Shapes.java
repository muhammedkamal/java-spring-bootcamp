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
    }
}

// TODO: abstract class Shape
// TODO: class Circle extends Shape
// TODO: class Rectangle extends Shape
// TODO: class Square extends Rectangle   (hint: a protected constructor in Rectangle that takes the name)
// TODO: class Triangle extends Shape
