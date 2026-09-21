package com.developeros.organizer.api;

import com.developeros.organizer.application.AttentionRefreshResult;

import java.util.List;
import java.util.UUID;

public record AttentionRefreshResponse(
        List<AttentionResponse> items,
        List<IntegrationErrorResponse> errors
) {
    public static AttentionRefreshResponse from(AttentionRefreshResult result) {
        return new AttentionRefreshResponse(
                result.items().stream().map(AttentionResponse::from).toList(),
                result.errors().stream().map(IntegrationErrorResponse::from).toList());
    }

    public record IntegrationErrorResponse(UUID projectId, String message) {
        private static IntegrationErrorResponse from(AttentionRefreshResult.IntegrationError error) {
            return new IntegrationErrorResponse(error.projectId(), error.message());
        }
    }
}
