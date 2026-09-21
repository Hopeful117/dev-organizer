package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.WorkItem;
import com.developeros.organizer.domain.WorkItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WorkItemRepositoryAdapter implements WorkItemRepository {
    private final WorkItemSpringDataRepository repository;

    public WorkItemRepositoryAdapter(WorkItemSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkItem save(WorkItem item) {
        return toDomain(repository.save(toEntity(item)));
    }

    @Override
    public Optional<WorkItem> findById(UUID id) {
        return repository.findById(id).map(WorkItemRepositoryAdapter::toDomain);
    }

    @Override
    public List<WorkItem> findAll() {
        return repository.findAll().stream().map(WorkItemRepositoryAdapter::toDomain).toList();
    }

    private static WorkItemJpaEntity toEntity(WorkItem item) {
        return new WorkItemJpaEntity(item.id(), item.title(), item.description(), item.projectId(), item.status(), item.createdAt(), item.updatedAt());
    }

    private static WorkItem toDomain(WorkItemJpaEntity entity) {
        return WorkItem.rehydrate(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getProjectId(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
