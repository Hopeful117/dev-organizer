package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.InboxItem;
import com.developeros.organizer.domain.InboxItemRepository;
import com.developeros.organizer.domain.InboxItemStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InboxItemRepositoryAdapter implements InboxItemRepository {
    private final InboxItemSpringDataRepository repository;

    public InboxItemRepositoryAdapter(InboxItemSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public InboxItem save(InboxItem item) {
        return toDomain(repository.save(toEntity(item)));
    }

    @Override
    public Optional<InboxItem> findById(UUID id) {
        return repository.findById(id).map(InboxItemRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<InboxItem> findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id).map(InboxItemRepositoryAdapter::toDomain);
    }

    @Override
    public List<InboxItem> findByStatus(InboxItemStatus status) {
        return repository.findByStatus(status).stream().map(InboxItemRepositoryAdapter::toDomain).toList();
    }

    private static InboxItemJpaEntity toEntity(InboxItem item) {
        return new InboxItemJpaEntity(item.id(), item.content(), item.projectId(), item.status(), item.createdAt());
    }

    private static InboxItem toDomain(InboxItemJpaEntity entity) {
        return InboxItem.rehydrate(entity.getId(), entity.getContent(), entity.getProjectId(), entity.getStatus(), entity.getCreatedAt());
    }
}
