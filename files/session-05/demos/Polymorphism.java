// One variable type, many behaviors: Java picks the method of the real object at run time.
public class Polymorphism {
    public static void main(String[] args) {
        Animal[] zoo = { new Dog("Rex"), new Cat("Mimi"), new Animal("Thing") };

        for (Animal a : zoo) {
            a.speak();                          // Rex says woof, Mimi says meow, Thing makes a sound
        }

        Animal pet = new Cat("Luna");           // upcast: always safe
        pet.speak();                            // Luna says meow (the object decides)

        // Downcast: only when the object really is a Cat
        if (pet instanceof Cat c) {
            c.purr();                           // Luna purrs
        }

        Animal other = new Dog("Max");
        // Cat wrong = (Cat) other;            // compiles, crashes: ClassCastException
        System.out.println(other instanceof Cat);   // false
    }
}

class Animal {
    protected String name;

    Animal(String name) {
        this.name = name;
    }

    void speak() {
        System.out.println(name + " makes a sound");
    }
}

class Dog extends Animal {
    Dog(String name) {
        super(name);
    }

    @Override
    void speak() {
        System.out.println(name + " says woof");
    }
}

class Cat extends Animal {
    Cat(String name) {
        super(name);
    }

    @Override
    void speak() {
        System.out.println(name + " says meow");
    }

    void purr() {
        System.out.println(name + " purrs");
    }
}
