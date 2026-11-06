// Lab 1: refactor to constructor injection. Run: java Lab1DiRefactor.java
//
// ReportService is tightly coupled to ConsoleLogger.
// 1. Introduce an interface `Logger` with one method: void log(String message).
// 2. Make ConsoleLogger implement it. Add a second class PrefixLogger(String prefix) that prints "<prefix> message".
// 3. Change ReportService so it receives a Logger through its CONSTRUCTOR (private final field, no `new` inside).
// 4. In main(), wire three services by hand: one with ConsoleLogger, one with PrefixLogger("[AUDIT]"),
//    one with a lambda that stores messages in a List, and print that list's size at the end.
//
// Expected output after the refactor:
//   Report: sales
//   [AUDIT] Report: sales
//   captured messages: 1
public class Lab1DiRefactor {

    static class ConsoleLogger {
        void log(String message) {
            System.out.println(message);
        }
    }

    static class ReportService {
        private final ConsoleLogger logger = new ConsoleLogger();

        void run(String name) {
            logger.log("Report: " + name);
        }
    }

    public static void main(String[] args) {
        new ReportService().run("sales");
        // TODO: wire the three versions by hand as described above
    }
}
