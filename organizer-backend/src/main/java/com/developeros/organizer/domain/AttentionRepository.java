package com.developeros.organizer.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttentionRepository {
    AttentionItem save(AttentionItem item);
    Optional<AttentionItem> findById(UUID id);
    Optional<AttentionItem> findByProjectAndSourceKey(UUID projectId, AttentionSource source, String sourceKey);
    List<AttentionItem> findCurrent();
    List<AttentionItem> findByProjectAndSource(UUID projectId, AttentionSource source);
}
