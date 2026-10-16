// The String methods you will use every week.
public class StringsDemo {
    public static void main(String[] args) {
        String s = "  Java Spring Boot  ";
        String t = s.trim();                         // "Java Spring Boot"

        System.out.println(t.length());              // 16
        System.out.println(t.charAt(0));             // J
        System.out.println(t.toUpperCase());
        System.out.println(t.substring(5));          // Spring Boot
        System.out.println(t.substring(0, 4));       // Java
        System.out.println(t.indexOf("Spring"));     // 5
        System.out.println(t.contains("Boot"));      // true
        System.out.println(t.replace(' ', '-'));     // Java-Spring-Boot

        String[] words = t.split(" ");
        System.out.println(words.length);            // 3
        System.out.println(words[1]);                // Spring

        System.out.println(t.equals("java spring boot"));        // false
        System.out.println(t.equalsIgnoreCase("java spring boot")); // true
    }
}
