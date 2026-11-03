// extends and super: a subclass reuses the parent's fields and methods, then adds its own.
public class Inheritance {
    public static void main(String[] args) {
        Dog rex = new Dog("Rex", 3, "Husky");
        rex.eat();                              // Rex is eating   (from Animal)
        rex.fetch();                            // Rex fetches the ball   (Dog only)
        System.out.println(rex.getName());      // Rex
        System.out.println(rex.breed);          // Husky

        Animal a = new Animal("Generic", 1);
        a.eat();                                // Generic is eating
        // a.fetch();                           // does not compile: an Animal cannot fetch
    }
}

class Animal {
    protected String name;                      // visible to subclasses
    private int age;

    Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    void eat() {
        System.out.println(name + " is eating");
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }
}

class Dog extends Animal {                      // a Dog IS an Animal
    String breed;

    Dog(String name, int age, String breed) {
        super(name, age);                       // run Animal's constructor first
        this.breed = breed;
    }

    void fetch() {
        System.out.println(name + " fetches the ball");
    }
}
