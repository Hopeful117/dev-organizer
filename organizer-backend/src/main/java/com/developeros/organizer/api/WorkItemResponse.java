package com.developeros.organizer.api;

import com.developeros.organizer.domain.WorkItem;

import java.time.Instant;
import java.util.UUID;

public record WorkItemResponse(UUID id, String title, String description, UUID projectId, String status,
                               Instant createdAt, Instant updatedAt) {
    public static WorkItemResponse from(WorkItem item) {
        return new WorkItemResponse(item.id(), item.title(), item.description(), item.projectId(), item.status().name(), item.createdAt(), item.updatedAt());
    }
}
