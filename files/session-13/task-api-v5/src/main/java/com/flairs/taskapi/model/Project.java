package com.flairs.taskapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// An entity is a class mapped to a table. One Project row has many Task rows (one-to-many).
@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // PostgreSQL BIGSERIAL gives the id
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    private String description;

    // mappedBy = "project": the foreign key lives on the Task side (tasks.project_id).
    // This side is only a view of it. Collections are LAZY by default: loaded only when used.
    @OneToMany(mappedBy = "project")
    private List<Task> tasks = new ArrayList<>();

    // NEW in Session 12: who owns this project. Only the owner (or an ADMIN) can see and change it.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Project() {
        // JPA needs a no-argument constructor
    }

    public Project(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    // Reading the id of a LAZY relation does not run a query: the proxy already knows it.
    public boolean isOwnedBy(Long userId) {
        return owner.getId().equals(userId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
