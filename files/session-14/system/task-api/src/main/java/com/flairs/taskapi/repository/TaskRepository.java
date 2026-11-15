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

    // Session 12: every query takes an ownerId. null means "everybody" (used for ADMIN).
    boolean existsByProjectIdAndTitleIgnoreCase(Long projectId, String title);

    @EntityGraph(attributePaths = "project")
    @Query("""
            select t from Task t
            where t.dueDate < :date and t.done = false
              and (:ownerId is null or t.project.owner.id = :ownerId)
            order by t.dueDate
            """)
    List<Task> findOverdue(@Param("date") LocalDate date, @Param("ownerId") Long ownerId);

    // 2. @Query with JPQL: queries on entities and fields, not tables and columns.
    //    Null filters are skipped, so one method serves every combination. Paging and sorting come from Pageable.
    //    @EntityGraph loads the project in the SAME query: this is the fix for the N+1 problem.
    @EntityGraph(attributePaths = "project")
    @Query("""
            select t from Task t
            where (:ownerId is null or t.project.owner.id = :ownerId)
              and (:done is null or t.done = :done)
              and (:priority is null or t.priority = :priority)
            """)
    Page<Task> search(@Param("ownerId") Long ownerId, @Param("done") Boolean done,
                      @Param("priority") Priority priority, Pageable pageable);

    // 3. JOIN FETCH: the JPQL way to load the relation in the same query
    @Query("select t from Task t join fetch t.project p where p.id = :projectId order by t.id")
    List<Task> findByProjectIdWithProject(@Param("projectId") Long projectId);

    @EntityGraph(attributePaths = {"project", "tags"})
    Optional<Task> findWithDetailsById(Long id);

    // 4. A projection into a record: only the columns we need, straight into a DTO
    @Query("""
            select new com.flairs.taskapi.dto.PriorityCount(t.priority, count(t)) from Task t
            where (:ownerId is null or t.project.owner.id = :ownerId)
            group by t.priority order by t.priority
            """)
    List<PriorityCount> countByPriority(@Param("ownerId") Long ownerId);
}
