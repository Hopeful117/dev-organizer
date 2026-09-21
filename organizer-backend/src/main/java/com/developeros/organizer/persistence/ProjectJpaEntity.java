package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.ProjectStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class ProjectJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "devlog_project_id")
    private UUID devlogProjectId;

    @Column(name = "devlog_project_slug", length = 100)
    private String devlogProjectSlug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProjectJpaEntity() {
    }

    public ProjectJpaEntity(UUID id, String name, ProjectStatus status, UUID devlogProjectId,
                            String devlogProjectSlug, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.devlogProjectId = devlogProjectId;
        this.devlogProjectSlug = devlogProjectSlug;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public ProjectStatus getStatus() { return status; }
    public UUID getDevlogProjectId() { return devlogProjectId; }
    public String getDevlogProjectSlug() { return devlogProjectSlug; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
