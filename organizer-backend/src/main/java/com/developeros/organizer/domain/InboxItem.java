package com.developeros.organizer.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class InboxItem {
    private final UUID id;
    private final String content;
    private UUID projectId;
    private InboxItemStatus status;
    private final Instant createdAt;

    private InboxItem(UUID id, String content, UUID projectId, InboxItemStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.content = requireText(content, "Inbox content must not be blank");
        this.projectId = projectId;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static InboxItem capture(String content, UUID projectId, Instant now) {
        return new InboxItem(UUID.randomUUID(), content, projectId, InboxItemStatus.CAPTURED, now);
    }

    public static InboxItem rehydrate(UUID id, String content, UUID projectId, InboxItemStatus status, Instant createdAt) {
        return new InboxItem(id, content, projectId, status, createdAt);
    }

    public void dismiss() {
        ensureCaptured();
        status = InboxItemStatus.DISMISSED;
    }

    public void promote() {
        ensureCaptured();
        status = InboxItemStatus.PROMOTED;
    }

    public void associateProject(UUID projectId) {
        ensureCaptured();
        this.projectId = projectId;
    }

    private void ensureCaptured() {
        if (status != InboxItemStatus.CAPTURED) {
            throw new DomainException("Inbox item is no longer captured");
        }
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public UUID id() { return id; }
    public String content() { return content; }
    public UUID projectId() { return projectId; }
    public InboxItemStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
}
