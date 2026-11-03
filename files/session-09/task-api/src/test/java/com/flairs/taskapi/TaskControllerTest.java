package com.flairs.taskapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// The controller is a normal Java class, so we can call its methods directly.
// (Week 7 shows the proper way: MockMvc, which also tests the HTTP layer.)
class TaskControllerTest {

    @Test
    void seedDataIsLoadedAndFiltersWork() {
        TaskController controller = new TaskController();
        assertEquals(3, controller.list(null, null).size());
        assertEquals(1, controller.list(true, null).size());
        assertEquals(1, controller.list(null, "SPRING").size());
    }

    @Test
    void createReturns201AndAssignsAnId() {
        TaskController controller = new TaskController();
        ResponseEntity<Task> response = controller.create(new Task(null, "Write tests", false));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(4L, response.getBody().id());
    }

    @Test
    void missingTaskGives404() {
        TaskController controller = new TaskController();
        assertEquals(HttpStatus.NOT_FOUND, controller.get(99L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.delete(99L).getStatusCode());
    }

    @Test
    void completeMarksTheTaskDone() {
        TaskController controller = new TaskController();
        controller.complete(1L);
        List<Task> done = controller.list(true, null);
        assertTrue(done.stream().anyMatch(t -> t.id() == 1L));
    }
}
