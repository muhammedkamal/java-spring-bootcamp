package com.flairs.taskapi.repository;

import com.flairs.taskapi.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

// No class to write: Spring Data generates the implementation at startup.
// JpaRepository gives save, findById, findAll (with paging and sorting), deleteById, count...
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByNameIgnoreCase(String name);   // derived query: Spring reads the method name
}
