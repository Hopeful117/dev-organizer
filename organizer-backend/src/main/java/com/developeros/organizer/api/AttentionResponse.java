package com.developeros.organizer.api;

import com.developeros.organizer.domain.AttentionItem;

import java.time.Instant;
import java.util.UUID;

public record AttentionResponse(
        UUID id,
        UUID projectId,
        String source,
        String reason,
        String guidance,
        Instant observedAt,
        String state,
        String sourceReference,
        Instant createdAt,
        Instant updatedAt
) {
    public static AttentionResponse from(AttentionItem item) {
        return new AttentionResponse(item.id(), item.projectId(), item.source().name(), item.reason(),
                item.guidance(), item.observedAt(), item.state().name(), item.sourceReference(),
                item.createdAt(), item.updatedAt());
    }
}
