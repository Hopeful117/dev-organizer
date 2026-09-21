package com.developeros.organizer.api;

import com.developeros.organizer.application.DevlogProjectCatalogPort;

import java.util.UUID;

public record DevlogProjectOptionResponse(UUID id, String name, String slug, String status) {
    public static DevlogProjectOptionResponse from(DevlogProjectCatalogPort.DevlogProjectOption project) {
        return new DevlogProjectOptionResponse(project.id(), project.name(), project.slug(), project.status());
    }
}
