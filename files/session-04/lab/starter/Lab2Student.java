// Lab 2: Student with validation, toString and equals.
// Rules: mark is kept between 0 and 100 (clamp). Two students are equal when their id is equal.
//        grade(): >= 90 A, >= 75 B, >= 60 C, otherwise Fail.
// Check (main below): 1001 Mona: 88 (B), true, 100
public class Lab2Student {
    public static void main(String[] args) {
        Student s1 = new Student("Mona", 1001, 88);
        Student s2 = new Student("Mona", 1001, 70);
        System.out.println(s1);
        System.out.println(s1.equals(s2));
        System.out.println(new Student("Omar", 1002, 140).getMark());
    }
}

class Student {
    // TODO: private fields name, id, mark
    // TODO: constructor (name, id, mark) that uses setMark
    // TODO: setMark, getMark, getName, grade()
    // TODO: toString, equals (by id), hashCode (by id)
}
