package com.developeros.organizer.application;

import com.developeros.organizer.domain.AttentionItem;

import java.util.List;
import java.util.UUID;

public record AttentionRefreshResult(List<AttentionItem> items, List<IntegrationError> errors) {
    public AttentionRefreshResult {
        items = List.copyOf(items);
        errors = List.copyOf(errors);
    }

    public record IntegrationError(UUID projectId, String message) { }
}
