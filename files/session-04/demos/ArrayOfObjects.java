// An array of objects holds references. Each slot starts as null.
public class ArrayOfObjects {
    public static void main(String[] args) {
        Pet[] pets = new Pet[3];
        System.out.println(pets[0]);                    // null

        pets[0] = new Pet("Mimi");
        pets[1] = new Pet("Tom");

        for (Pet p : pets) {
            if (p != null) {                            // guard against null slots
                System.out.println(p.name);
            }
        }
    }
}

class Pet {
    String name;

    Pet(String name) {
        this.name = name;
    }
}
