package com.developeros.organizer.api;

import com.developeros.organizer.domain.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String status, DevlogProjectResponse devlogProject,
                              Instant createdAt, Instant updatedAt) {
    public static ProjectResponse from(Project project) {
        var devlogProject = project.devlogProject();
        return new ProjectResponse(project.id(), project.name(), project.status().name(),
                devlogProject == null ? null : new DevlogProjectResponse(devlogProject.id(), devlogProject.slug()),
                project.createdAt(), project.updatedAt());
    }

    public record DevlogProjectResponse(UUID id, String slug) { }
}
