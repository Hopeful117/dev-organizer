package com.developeros.organizer.application;

import com.developeros.organizer.domain.Project;
import com.developeros.organizer.domain.ProjectRepository;
import com.developeros.organizer.domain.DevlogProjectReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectApplicationService {
    private final ProjectRepository projectRepository;
    private final Clock clock;

    public ProjectApplicationService(ProjectRepository projectRepository, Clock clock) {
        this.projectRepository = projectRepository;
        this.clock = clock;
    }

    public Project create(String name) {
        return projectRepository.save(Project.create(name, Instant.now(clock)));
    }

    @Transactional(readOnly = true)
    public List<Project> list() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Project get(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    public Project archive(UUID id) {
        Project project = get(id);
        project.archive(Instant.now(clock));
        return projectRepository.save(project);
    }

    public void requireExists(UUID id) {
        if (id != null && projectRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
    }

    public Project linkDevlogProject(UUID projectId, UUID devlogProjectId, String devlogProjectSlug) {
        Project project = get(projectId);
        project.linkDevlogProject(new DevlogProjectReference(devlogProjectId, devlogProjectSlug), Instant.now(clock));
        return projectRepository.save(project);
    }

    public Project unlinkDevlogProject(UUID projectId) {
        Project project = get(projectId);
        project.unlinkDevlogProject(Instant.now(clock));
        return projectRepository.save(project);
    }
}
