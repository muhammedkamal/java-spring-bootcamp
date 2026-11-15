package com.flairs.taskapi.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flairs.taskapi.TestcontainersConfiguration;
import com.flairs.taskapi.config.JpaConfig;
import com.flairs.taskapi.dto.PriorityCount;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// A SLICE test for the data layer: JPA, Flyway and the repositories, on a REAL PostgreSQL (Testcontainers).
// Flyway runs V1..V8, so the seed data is there: Sara (id 2) owns projects 1 and 2 with 6 tasks, Omar (id 3) owns
// project 3 with none. Every test runs in a transaction that is rolled back at the end, so tests do not see each
// other's changes.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // keep PostgreSQL, no H2
@Import({TestcontainersConfiguration.class, JpaConfig.class})                     // JpaConfig: @CreatedDate
class TaskRepositoryTest {

    @Autowired TaskRepository tasks;
    @Autowired ProjectRepository projects;

    @Test
    void searchOnlyReturnsTheOwnersTasks() {
        var sara = tasks.search(2L, null, null, PageRequest.of(0, 20));
        var omar = tasks.search(3L, null, null, PageRequest.of(0, 20));
        var admin = tasks.search(null, null, null, PageRequest.of(0, 20));

        assertThat(sara.getTotalElements()).isEqualTo(6);
        assertThat(omar.getTotalElements()).isZero();
        assertThat(admin.getTotalElements()).isEqualTo(6);
    }

    @Test
    void searchFiltersAndPages() {
        var page = tasks.search(2L, false, Priority.HIGH, PageRequest.of(0, 1, Sort.by("title")));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Task::getTitle).containsExactly("Move the API to PostgreSQL");
    }

    @Test
    void overdueFindsOpenTasksBeforeTheDate() {
        List<Task> overdue = tasks.findOverdue(LocalDate.now().plusDays(4), 2L);

        // due in 1, 2 and 3 days and not done; "Read about the N+1 problem" is due in 5
        assertThat(overdue).extracting(Task::getTitle)
                .containsExactly("Buy groceries", "Move the API to PostgreSQL", "Pay the internet bill");
    }

    @Test
    void countByPriorityIsAProjection() {
        assertThat(tasks.countByPriority(2L)).containsExactlyInAnyOrder(
                new PriorityCount(Priority.LOW, 1), new PriorityCount(Priority.MEDIUM, 2), new PriorityCount(Priority.HIGH, 3));
    }

    @Test
    void theDatabaseRejectsADuplicateTitleInOneProject() {
        var project = projects.findById(1L).orElseThrow();

        // the unique index is on lower(title): a different case is still a duplicate
        assertThatThrownBy(() -> tasks.saveAndFlush(new Task("LEARN SPRING", null, Priority.LOW, null, null, project)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
