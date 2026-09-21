package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.Project;
import com.developeros.organizer.domain.DevlogProjectReference;
import com.developeros.organizer.domain.ProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {
    private final ProjectSpringDataRepository repository;

    public ProjectRepositoryAdapter(ProjectSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project save(Project project) {
        return toDomain(repository.save(toEntity(project)));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return repository.findById(id).map(ProjectRepositoryAdapter::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return repository.findAll().stream().map(ProjectRepositoryAdapter::toDomain).toList();
    }

    private static ProjectJpaEntity toEntity(Project project) {
        var devlogProject = project.devlogProject();
        return new ProjectJpaEntity(project.id(), project.name(), project.status(),
                devlogProject == null ? null : devlogProject.id(),
                devlogProject == null ? null : devlogProject.slug(),
                project.createdAt(), project.updatedAt());
    }

    private static Project toDomain(ProjectJpaEntity entity) {
        DevlogProjectReference devlogProject = entity.getDevlogProjectId() == null ? null
                : new DevlogProjectReference(entity.getDevlogProjectId(), entity.getDevlogProjectSlug());
        return Project.rehydrate(entity.getId(), entity.getName(), entity.getStatus(), devlogProject,
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
