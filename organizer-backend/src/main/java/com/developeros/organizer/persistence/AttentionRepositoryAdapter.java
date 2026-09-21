package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.AttentionItem;
import com.developeros.organizer.domain.AttentionRepository;
import com.developeros.organizer.domain.AttentionSource;
import com.developeros.organizer.domain.AttentionState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AttentionRepositoryAdapter implements AttentionRepository {
    private final AttentionSpringDataRepository repository;

    public AttentionRepositoryAdapter(AttentionSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public AttentionItem save(AttentionItem item) {
        return toDomain(repository.save(toEntity(item)));
    }

    @Override
    public Optional<AttentionItem> findById(UUID id) {
        return repository.findById(id).map(AttentionRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<AttentionItem> findByProjectAndSourceKey(UUID projectId,
                                                               AttentionSource source,
                                                               String sourceKey) {
        return repository.findByProjectIdAndSourceAndSourceKey(projectId, source, sourceKey)
                .map(AttentionRepositoryAdapter::toDomain);
    }

    @Override
    public List<AttentionItem> findCurrent() {
        return repository.findByStateInOrderByObservedAtDesc(
                        List.of(AttentionState.OPEN, AttentionState.ACKNOWLEDGED))
                .stream().map(AttentionRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<AttentionItem> findByProjectAndSource(UUID projectId, AttentionSource source) {
        return repository.findByProjectIdAndSource(projectId, source)
                .stream().map(AttentionRepositoryAdapter::toDomain).toList();
    }

    private static AttentionJpaEntity toEntity(AttentionItem item) {
        return new AttentionJpaEntity(item.id(), item.projectId(), item.source(), item.sourceKey(),
                item.reason(), item.guidance(), item.observedAt(), item.state(), item.sourceReference(),
                item.createdAt(), item.updatedAt());
    }

    private static AttentionItem toDomain(AttentionJpaEntity entity) {
        return AttentionItem.rehydrate(entity.getId(), entity.getProjectId(), entity.getSource(),
                entity.getSourceKey(), entity.getReason(), entity.getGuidance(), entity.getObservedAt(),
                entity.getState(), entity.getSourceReference(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
