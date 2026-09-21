package com.developeros.organizer.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Project {
    private final UUID id;
    private final String name;
    private ProjectStatus status;
    private DevlogProjectReference devlogProject;
    private final Instant createdAt;
    private Instant updatedAt;

    private Project(UUID id, String name, ProjectStatus status, DevlogProjectReference devlogProject,
                    Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.name = requireText(name, "Project name must not be blank");
        this.status = Objects.requireNonNull(status);
        this.devlogProject = devlogProject;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Project create(String name, Instant now) {
        return new Project(UUID.randomUUID(), name, ProjectStatus.ACTIVE, null, now, now);
    }

    public static Project rehydrate(UUID id, String name, ProjectStatus status,
                                    DevlogProjectReference devlogProject,
                                    Instant createdAt, Instant updatedAt) {
        return new Project(id, name, status, devlogProject, createdAt, updatedAt);
    }

    public void archive(Instant now) {
        if (status == ProjectStatus.ARCHIVED) {
            return;
        }
        status = ProjectStatus.ARCHIVED;
        updatedAt = Objects.requireNonNull(now);
    }

    public void linkDevlogProject(DevlogProjectReference reference, Instant now) {
        devlogProject = Objects.requireNonNull(reference);
        updatedAt = Objects.requireNonNull(now);
    }

    public void unlinkDevlogProject(Instant now) {
        devlogProject = null;
        updatedAt = Objects.requireNonNull(now);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public ProjectStatus status() { return status; }
    public DevlogProjectReference devlogProject() { return devlogProject; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
