package com.flairs.taskapi.service;

import com.flairs.taskapi.exception.DuplicateTaskException;
import com.flairs.taskapi.exception.TaskNotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

// The business layer: rules live here ("no duplicate titles", "not found"). It knows nothing about HTTP.
@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
        // seed data
        repository.save(new Task(null, "Learn Spring", "IoC, beans, REST", Priority.HIGH, false, null));
        repository.save(new Task(null, "Build the Task API", null, Priority.MEDIUM, false, LocalDate.now().plusDays(7)));
        repository.save(new Task(null, "Install JDK 25", null, Priority.LOW, true, null));
    }

    public List<Task> findAll(Boolean done, Priority priority) {
        return repository.findAll().stream()
                .filter(t -> done == null || t.done() == done)
                .filter(t -> priority == null || t.priority() == priority)
                .toList();
    }

    public Task findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        if (repository.existsByTitle(task.title())) {
            throw new DuplicateTaskException(task.title());
        }
        return repository.save(task);
    }

    public Task update(Long id, Task task) {
        findById(id);                           // throws TaskNotFoundException if missing
        return repository.save(task);
    }

    public Task complete(Long id) {
        return repository.save(findById(id).withDone(true));
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }
}
