package com.developeros.organizer.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class AttentionItem {
    private final UUID id;
    private final UUID projectId;
    private final AttentionSource source;
    private final String sourceKey;
    private String reason;
    private String guidance;
    private Instant observedAt;
    private AttentionState state;
    private String sourceReference;
    private final Instant createdAt;
    private Instant updatedAt;

    private AttentionItem(UUID id, UUID projectId, AttentionSource source, String sourceKey,
                          String reason, String guidance, Instant observedAt, AttentionState state,
                          String sourceReference, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.projectId = Objects.requireNonNull(projectId);
        this.source = Objects.requireNonNull(source);
        this.sourceKey = requireText(sourceKey, "Attention source key must not be blank");
        this.reason = requireText(reason, "Attention reason must not be blank");
        this.guidance = requireText(guidance, "Attention guidance must not be blank");
        this.observedAt = Objects.requireNonNull(observedAt);
        this.state = Objects.requireNonNull(state);
        this.sourceReference = requireText(sourceReference, "Attention source reference must not be blank");
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static AttentionItem open(UUID projectId, AttentionSource source, String sourceKey,
                                     String reason, String guidance, Instant observedAt,
                                     String sourceReference, Instant now) {
        return new AttentionItem(UUID.randomUUID(), projectId, source, sourceKey, reason, guidance,
                observedAt, AttentionState.OPEN, sourceReference, now, now);
    }

    public static AttentionItem rehydrate(UUID id, UUID projectId, AttentionSource source,
                                          String sourceKey, String reason, String guidance,
                                          Instant observedAt, AttentionState state,
                                          String sourceReference, Instant createdAt, Instant updatedAt) {
        return new AttentionItem(id, projectId, source, sourceKey, reason, guidance, observedAt,
                state, sourceReference, createdAt, updatedAt);
    }

    public void reobserve(String nextReason, String nextGuidance, Instant nextObservedAt,
                          String nextSourceReference, Instant now) {
        reason = requireText(nextReason, "Attention reason must not be blank");
        guidance = requireText(nextGuidance, "Attention guidance must not be blank");
        observedAt = Objects.requireNonNull(nextObservedAt);
        sourceReference = requireText(nextSourceReference, "Attention source reference must not be blank");
        if (state == AttentionState.RESOLVED) state = AttentionState.OPEN;
        updatedAt = Objects.requireNonNull(now);
    }

    public void acknowledge(Instant now) {
        if (state == AttentionState.OPEN) {
            state = AttentionState.ACKNOWLEDGED;
            updatedAt = Objects.requireNonNull(now);
        }
    }

    public void dismiss(Instant now) {
        if (state == AttentionState.OPEN || state == AttentionState.ACKNOWLEDGED) {
            state = AttentionState.DISMISSED;
            updatedAt = Objects.requireNonNull(now);
        }
    }

    public void resolve(Instant now) {
        if (state == AttentionState.OPEN || state == AttentionState.ACKNOWLEDGED) {
            state = AttentionState.RESOLVED;
            updatedAt = Objects.requireNonNull(now);
        }
    }

    public UUID id() { return id; }
    public UUID projectId() { return projectId; }
    public AttentionSource source() { return source; }
    public String sourceKey() { return sourceKey; }
    public String reason() { return reason; }
    public String guidance() { return guidance; }
    public Instant observedAt() { return observedAt; }
    public AttentionState state() { return state; }
    public String sourceReference() { return sourceReference; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
