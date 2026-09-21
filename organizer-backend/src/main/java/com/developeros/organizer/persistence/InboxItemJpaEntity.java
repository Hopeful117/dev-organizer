package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.InboxItemStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox_items")
public class InboxItemJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "project_id")
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InboxItemStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InboxItemJpaEntity() {
    }

    public InboxItemJpaEntity(UUID id, String content, UUID projectId, InboxItemStatus status, Instant createdAt) {
        this.id = id;
        this.content = content;
        this.projectId = projectId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getContent() { return content; }
    public UUID getProjectId() { return projectId; }
    public InboxItemStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
