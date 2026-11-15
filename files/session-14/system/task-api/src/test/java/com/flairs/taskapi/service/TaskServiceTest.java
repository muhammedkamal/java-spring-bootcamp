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
import com.flairs.taskapi.model.Role;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.model.User;
import com.flairs.taskapi.repository.TagRepository;
import com.flairs.taskapi.repository.TaskRepository;
import com.flairs.taskapi.events.TaskEvent;
import com.flairs.taskapi.security.AuthUser;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    static final AuthUser SARA = new AuthUser(2L, "sara@flairs.dev", Role.USER);
    static final AuthUser OMAR = new AuthUser(3L, "omar@flairs.dev", Role.USER);
    static final AuthUser ADMIN = new AuthUser(1L, "admin@flairs.dev", Role.ADMIN);

    @Mock TaskRepository tasks;
    @Mock ProjectService projects;
    @Mock TagRepository tags;
    // Session 14: the service now also publishes events and reads the time. Without these two mocks
    // @InjectMocks would pass null for them and complete() would fail with a NullPointerException.
    @Mock ApplicationEventPublisher events;
    @Spy Clock clock = Clock.fixed(Instant.parse("2026-01-01T09:00:00Z"), ZoneOffset.UTC);
    @InjectMocks TaskService service;

    static Project projectOwnedBy(long ownerId) {
        User owner = new User("x@flairs.dev", "hash", "X", Role.USER);
        ReflectionTestUtils.setField(owner, "id", ownerId);     // the database sets ids; in a unit test we do
        Project project = new Project("Internship", null, owner);
        ReflectionTestUtils.setField(project, "id", 10L);
        return project;
    }

    @Test
    void createRejectsADuplicateTitleInTheSameProject() {
        when(projects.getVisible(10L, SARA)).thenReturn(projectOwnedBy(2));
        when(tasks.existsByProjectIdAndTitleIgnoreCase(10L, "Learn Spring")).thenReturn(true);
        var request = new CreateTaskRequest(" Learn Spring ", null, Priority.HIGH, null, null, 10L, null);

        assertThatThrownBy(() -> service.create(request, SARA)).isInstanceOf(DuplicateException.class);
        verify(tasks, never()).save(any());
    }

    @Test
    void theOwnerCanCompleteTheirTask() {
        when(tasks.findById(5L)).thenReturn(Optional.of(new Task("T", null, Priority.LOW, null, null, projectOwnedBy(2))));

        assertThat(service.complete(5L, SARA).done()).isTrue();
    }

    @Test
    void completingATaskPublishesACompletedEvent() {
        when(tasks.findById(5L)).thenReturn(Optional.of(new Task("T", null, Priority.LOW, null, null, projectOwnedBy(2))));

        service.complete(5L, SARA);

        ArgumentCaptor<TaskEvent> event = ArgumentCaptor.forClass(TaskEvent.class);
        verify(events).publishEvent(event.capture());
        assertThat(event.getValue().type()).isEqualTo(TaskEvent.Type.COMPLETED);
        assertThat(event.getValue().ownerId()).isEqualTo(2L);
        assertThat(event.getValue().occurredAt()).isEqualTo(Instant.parse("2026-01-01T09:00:00Z"));
    }

    @Test
    void completingATaskTwicePublishesOnlyOnce() {
        Task task = new Task("T", null, Priority.LOW, null, null, projectOwnedBy(2));
        when(tasks.findById(5L)).thenReturn(Optional.of(task));

        service.complete(5L, SARA);
        service.complete(5L, SARA);

        verify(events).publishEvent(any(TaskEvent.class));   // verify(...) means exactly once
    }

    @Test
    void someoneElsesTaskLooksLikeItDoesNotExist() {
        Task task = new Task("T", null, Priority.LOW, null, null, projectOwnedBy(2));
        ReflectionTestUtils.setField(task, "id", 5L);
        when(tasks.findById(5L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.complete(5L, OMAR)).isInstanceOf(NotFoundException.class).hasMessage("Task 5 not found");
        assertThat(task.isDone()).isFalse();
    }

    @Test
    void anAdminCanTouchAnyTask() {
        when(tasks.findById(5L)).thenReturn(Optional.of(new Task("T", null, Priority.LOW, null, null, projectOwnedBy(2))));

        assertThat(service.complete(5L, ADMIN).done()).isTrue();
    }

    @Test
    void searchFiltersByOwnerExceptForAdmins() {
        assertThat(SARA.ownerFilter()).isEqualTo(2L);
        assertThat(ADMIN.ownerFilter()).isNull();
    }
}
