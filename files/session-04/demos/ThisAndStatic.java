// Instance members belong to an object. static members belong to the class.
public class ThisAndStatic {
    public static void main(String[] args) {
        Ticket a = new Ticket();
        Ticket b = new Ticket();
        Ticket c = new Ticket();
        System.out.println(a.number + " " + b.number + " " + c.number);   // 1 2 3
        System.out.println("Issued: " + Ticket.issued);                  // 3
        System.out.println(Ticket.isValid(b.number));                    // true
    }
}

class Ticket {
    static int issued = 0;      // one copy, shared by all tickets
    int number;                 // one copy per ticket

    Ticket() {
        issued++;
        number = issued;
    }

    static boolean isValid(int n) {   // no object needed, so it cannot use "number"
        return n > 0 && n <= issued;
    }
}
