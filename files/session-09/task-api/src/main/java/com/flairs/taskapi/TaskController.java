package com.flairs.taskapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Session 9 version: everything in one class, data in memory.
// It works, but it mixes web, logic and storage. Session 10 splits it into controller, service and repository.
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // Thread-safe, because Tomcat serves each request on its own thread
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public TaskController() {
        save(new Task(null, "Learn Spring", false));
        save(new Task(null, "Build the Task API", false));
        save(new Task(null, "Install JDK 25", true));
    }

    private Task save(Task task) {
        long id = nextId.getAndIncrement();
        Task saved = new Task(id, task.title(), task.done());
        tasks.put(id, saved);
        return saved;
    }

    // GET /api/tasks                 all tasks
    // GET /api/tasks?done=true       only finished tasks
    // GET /api/tasks?search=spring   title contains "spring" (ignore case)
    @GetMapping
    public List<Task> list(@RequestParam(required = false) Boolean done,
                           @RequestParam(required = false) String search) {
        List<Task> result = new ArrayList<>(tasks.values());
        if (done != null) {
            result.removeIf(t -> !done.equals(t.done()));
        }
        if (search != null) {
            result.removeIf(t -> !t.title().toLowerCase().contains(search.toLowerCase()));
        }
        result.sort(Comparator.comparing(Task::id));
        return result;
    }

    // GET /api/tasks/2
    // ResponseEntity lets us choose the status code: 200 with a body, or 404 with none
    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        Task task = tasks.get(id);
        return task == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(task);
    }

    // POST /api/tasks   body: {"title":"Write tests"}
    // 201 Created + Location header pointing at the new resource
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task body) {
        Task saved = save(body);
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.id())).body(saved);
    }

    // PUT /api/tasks/2   body: {"title":"New title","done":true}   replaces the whole task
    @PutMapping("/{id}")
    public ResponseEntity<Task> replace(@PathVariable Long id, @RequestBody Task body) {
        if (!tasks.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        Task updated = new Task(id, body.title(), body.done());
        tasks.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // PATCH /api/tasks/2/complete   changes one thing only
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> complete(@PathVariable Long id) {
        Task existing = tasks.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Task updated = new Task(id, existing.title(), true);
        tasks.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/tasks/2   204 No Content on success
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return tasks.remove(id) == null ? ResponseEntity.notFound().build() : ResponseEntity.noContent().build();
    }
}
