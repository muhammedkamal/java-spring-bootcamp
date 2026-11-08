package com.flairs.taskapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.taskapi.exception.TaskNotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// A preview of Week 7. @WebMvcTest starts only the web layer, and the service is replaced by a Mockito mock.
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TaskService service;

    @Test
    void invalidBodyGives400WithAFieldErrorMap() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("title is required"))
                .andExpect(jsonPath("$.errors.priority").exists());
        verifyNoInteractions(service);                         // validation stopped the request before the service
    }

    @Test
    void validBodyGives201AndLocation() throws Exception {
        when(service.create(any())).thenReturn(new Task(7L, "Write docs", null, Priority.LOW, false, null));
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write docs\",\"priority\":\"LOW\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void unknownTaskGives404AsProblemDetail() throws Exception {
        when(service.findById(99L)).thenThrow(new TaskNotFoundException(99L));
        mvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Task not found"))
                .andExpect(jsonPath("$.detail").value("Task 99 not found"));
    }
}
