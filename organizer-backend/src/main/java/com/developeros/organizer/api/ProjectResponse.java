package com.developeros.organizer.api;

import com.developeros.organizer.domain.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String status, Instant createdAt, Instant updatedAt) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.id(), project.name(), project.status().name(), project.createdAt(), project.updatedAt());
    }
}
