package com.flairs.taskapi.repository;

import com.flairs.taskapi.dto.PriorityCount;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 1. Derived queries: the method name IS the query.
    //    SELECT ... WHERE lower(title) = lower(?)
    boolean existsByTitleIgnoreCase(String title);

    //    SELECT ... WHERE due_date < ? AND done = false ORDER BY due_date
    List<Task> findByDueDateBeforeAndDoneFalseOrderByDueDate(LocalDate date);

    // 2. @Query with JPQL: queries on entities and fields, not tables and columns.
    //    Null filters are skipped, so one method serves every combination. Paging and sorting come from Pageable.
    //    @EntityGraph loads the project in the SAME query: this is the fix for the N+1 problem.
    @EntityGraph(attributePaths = "project")
    @Query("""
            select t from Task t
            where (:done is null or t.done = :done)
              and (:priority is null or t.priority = :priority)
            """)
    Page<Task> search(@Param("done") Boolean done, @Param("priority") Priority priority, Pageable pageable);

    // 3. JOIN FETCH: the JPQL way to load the relation in the same query
    @Query("select t from Task t join fetch t.project p where p.id = :projectId order by t.id")
    List<Task> findByProjectIdWithProject(@Param("projectId") Long projectId);

    @EntityGraph(attributePaths = {"project", "tags"})
    Optional<Task> findWithDetailsById(Long id);

    // 4. A projection into a record: only the columns we need, straight into a DTO
    @Query("select new com.flairs.taskapi.dto.PriorityCount(t.priority, count(t)) from Task t group by t.priority order by t.priority")
    List<PriorityCount> countByPriority();
}
