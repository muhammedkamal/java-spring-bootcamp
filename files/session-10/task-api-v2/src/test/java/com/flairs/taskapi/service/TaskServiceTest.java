package com.flairs.taskapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flairs.taskapi.exception.DuplicateTaskException;
import com.flairs.taskapi.exception.TaskNotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// A layered design pays off: the service is tested with no web server and no Spring at all.
class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new TaskRepository());      // 3 seed tasks
    }

    @Test
    void findAllCanFilterByDoneAndPriority() {
        assertEquals(3, service.findAll(null, null).size());
        assertEquals(1, service.findAll(true, null).size());
        assertEquals(1, service.findAll(null, Priority.HIGH).size());
    }

    @Test
    void createAssignsIdAndRejectsDuplicateTitles() {
        Task created = service.create(new Task(null, "Write docs", null, Priority.LOW, false, null));
        assertEquals(4L, created.id());
        assertThrows(DuplicateTaskException.class,
                () -> service.create(new Task(null, "WRITE DOCS", null, Priority.LOW, false, null)));
    }

    @Test
    void missingTaskThrowsNotFound() {
        assertThrows(TaskNotFoundException.class, () -> service.findById(99L));
        assertThrows(TaskNotFoundException.class, () -> service.delete(99L));
        assertThrows(TaskNotFoundException.class, () -> service.complete(99L));
    }

    @Test
    void completeSetsDone() {
        assertTrue(service.complete(1L).done());
    }
}
