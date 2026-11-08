package com.flairs.taskapi.repository;

import com.flairs.taskapi.model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

// The storage layer: it only stores and finds. No business rules, no HTTP.
// In Week 6 this class is replaced by a Spring Data JPA interface and the rest of the app does not change.
@Repository
public class TaskRepository {

    private final Map<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Task> findAll() {
        List<Task> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(Task::id));
        return all;
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsByTitle(String title) {
        return store.values().stream().anyMatch(t -> t.title().equalsIgnoreCase(title));
    }

    public Task save(Task task) {
        Task toStore = task.id() == null ? task.withId(nextId.getAndIncrement()) : task;
        store.put(toStore.id(), toStore);
        return toStore;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
