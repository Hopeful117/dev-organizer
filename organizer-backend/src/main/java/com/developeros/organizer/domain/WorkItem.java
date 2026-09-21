package com.developeros.organizer.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class WorkItem {
    private final UUID id;
    private final String title;
    private final String description;
    private UUID projectId;
    private WorkItemStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private WorkItem(UUID id, String title, String description, UUID projectId, WorkItemStatus status,
                     Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.title = requireText(title, "Work title must not be blank");
        this.description = description == null ? null : description.trim();
        this.projectId = projectId;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static WorkItem create(String title, String description, UUID projectId, Instant now) {
        return new WorkItem(UUID.randomUUID(), title, description, projectId, WorkItemStatus.TODO, now, now);
    }

    public static WorkItem rehydrate(UUID id, String title, String description, UUID projectId, WorkItemStatus status,
                                     Instant createdAt, Instant updatedAt) {
        return new WorkItem(id, title, description, projectId, status, createdAt, updatedAt);
    }

    public void associateProject(UUID projectId, Instant now) {
        this.projectId = projectId;
        this.updatedAt = Objects.requireNonNull(now);
    }

    public void start(Instant now) {
        if (status != WorkItemStatus.TODO) {
            throw new DomainException("Only TODO work can be started");
        }
        status = WorkItemStatus.IN_PROGRESS;
        updatedAt = Objects.requireNonNull(now);
    }

    public void complete(Instant now) {
        if (status != WorkItemStatus.IN_PROGRESS) {
            throw new DomainException("Only in-progress work can be completed");
        }
        status = WorkItemStatus.DONE;
        updatedAt = Objects.requireNonNull(now);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public UUID id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public UUID projectId() { return projectId; }
    public WorkItemStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
