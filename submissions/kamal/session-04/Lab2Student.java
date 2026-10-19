//import java.util.Objects;
//

import java.util.Objects;

//// Lab 2: Student with validation, toString and equals.
//// Rules: mark is kept between 0 and 100 (clamp). Two students are equal when their id is equal.
////        grade(): >= 90 A, >= 75 B, >= 60 C, otherwise Fail.
//// Check (main below): 1001 Mona: 88 (B), true, 100
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
    private String name;
    private int id;
    private int mark;
    // TODO: constructor (name, id, mark) that uses setMark
    Student(String name, int id, int mark){
        this.name = name;
        this.id = id;
        setMark(mark);
    }
    // TODO: setMark, getMark, getName, grade()
    public void setMark(int mark) {
        if (mark < 0 ){
            this.mark = 0;
        } else if (mark > 100) {
            this.mark = 100;
        }else{
            this.mark = mark;
        }
    }
    public int getMark() {
        return mark;
    }
    public String getName() {
        return name;
    }
    String grade(){
        if (mark >= 90) {
            return "A";
        } else if (mark >= 75) {
            return "B";
        } else if (mark >= 60) {
            return "C";
        } else {
            return "Fail";
        }
    }
    // TODO: toString, equals (by id), hashCode (by id)
    @Override
    public String toString(){
        return  String.format("%d %s: %d (%s)",id,name,mark,grade());
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Student other)) return false;
        return id == other.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
