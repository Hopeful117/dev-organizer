package com.developeros.organizer.api;

import com.developeros.organizer.domain.InboxItem;

import java.time.Instant;
import java.util.UUID;

public record InboxItemResponse(UUID id, String content, UUID projectId, String status, Instant createdAt) {
    public static InboxItemResponse from(InboxItem item) {
        return new InboxItemResponse(item.id(), item.content(), item.projectId(), item.status().name(), item.createdAt());
    }
}
