package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.AttentionSource;
import com.developeros.organizer.domain.AttentionState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attention_items")
public class AttentionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttentionSource source;
    @Column(name = "source_key", nullable = false, length = 1000)
    private String sourceKey;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(nullable = false, length = 80)
    private String guidance;
    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttentionState state;
    @Column(name = "source_reference", nullable = false, length = 500)
    private String sourceReference;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AttentionJpaEntity() {
    }

    public AttentionJpaEntity(UUID id, UUID projectId, AttentionSource source, String sourceKey,
                              String reason, String guidance, Instant observedAt, AttentionState state,
                              String sourceReference, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.source = source;
        this.sourceKey = sourceKey;
        this.reason = reason;
        this.guidance = guidance;
        this.observedAt = observedAt;
        this.state = state;
        this.sourceReference = sourceReference;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public AttentionSource getSource() { return source; }
    public String getSourceKey() { return sourceKey; }
    public String getReason() { return reason; }
    public String getGuidance() { return guidance; }
    public Instant getObservedAt() { return observedAt; }
    public AttentionState getState() { return state; }
    public String getSourceReference() { return sourceReference; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
