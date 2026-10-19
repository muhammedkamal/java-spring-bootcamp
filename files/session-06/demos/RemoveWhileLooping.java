import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Predict the output: removing from a list inside a for-each loop.
public class RemoveWhileLooping {
    public static void main(String[] args) {
        List<String> tasks = new ArrayList<>(List.of("done", "todo", "done", "todo"));

        try {
            for (String t : tasks) {
                if (t.equals("done")) {
                    tasks.remove(t);
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException");
        }

        // Fix 1: an Iterator, and remove through it
        Iterator<String> it = tasks.iterator();
        while (it.hasNext()) {
            if (it.next().equals("done")) {
                it.remove();
            }
        }
        System.out.println(tasks);                  // [todo, todo]

        // Fix 2 (shorter): tasks.removeIf(t -> t.equals("done"));
    }
}
