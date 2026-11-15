package com.flairs.taskapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flairs.taskapi.dto.CreateTaskRequest;
import com.flairs.taskapi.exception.DuplicateException;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Project;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.repository.ProjectRepository;
import com.flairs.taskapi.repository.TagRepository;
import com.flairs.taskapi.repository.TaskRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Unit test: no Spring, no database. The repositories are Mockito mocks. Runs in milliseconds.
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository tasks;
    @Mock ProjectRepository projects;
    @Mock TagRepository tags;
    @InjectMocks TaskService service;

    @Test
    void createRejectsADuplicateTitle() {
        when(tasks.existsByTitleIgnoreCase("Learn Spring")).thenReturn(true);
        var request = new CreateTaskRequest(" Learn Spring ", null, Priority.HIGH, null, null, 1L, null);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(DuplicateException.class);
        verify(tasks, never()).save(any());
    }

    @Test
    void createFailsWhenTheProjectDoesNotExist() {
        when(projects.findById(99L)).thenReturn(Optional.empty());
        var request = new CreateTaskRequest("New", null, Priority.LOW, null, null, 99L, null);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(NotFoundException.class).hasMessage("Project 99 not found");
    }

    @Test
    void createNormalisesTagNames() {
        when(projects.findById(1L)).thenReturn(Optional.of(new Project("Internship", null)));
        when(tags.findByName(any())).thenReturn(Optional.empty());
        when(tags.save(any())).thenAnswer(call -> call.getArgument(0));
        when(tasks.save(any())).thenAnswer(call -> call.getArgument(0));
        var request = new CreateTaskRequest("New", null, Priority.LOW, null, null, 1L, Set.of(" Java ", "JAVA", "docker"));

        var response = service.create(request);

        assertThat(response.tags()).containsExactly("docker", "java");
        assertThat(response.projectName()).isEqualTo("Internship");
    }

    @Test
    void completeMarksTheTaskDone() {
        Task task = new Task("T", null, Priority.LOW, null, null, new Project("P", null));
        when(tasks.findById(5L)).thenReturn(Optional.of(task));

        assertThat(service.complete(5L).done()).isTrue();
    }
}
