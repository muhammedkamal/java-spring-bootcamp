package com.flairs.taskapi.repository;

import com.flairs.taskapi.model.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // NEW in Session 12: queries are scoped to the owner. owner.id walks the relation: owner_id = ?
    List<Project> findByOwnerIdOrderByName(Long ownerId);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
}
