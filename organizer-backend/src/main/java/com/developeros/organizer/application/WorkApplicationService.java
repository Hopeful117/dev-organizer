package com.developeros.organizer.application;

import com.developeros.organizer.domain.ProjectRepository;
import com.developeros.organizer.domain.WorkItem;
import com.developeros.organizer.domain.WorkItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WorkApplicationService {
    private final WorkItemRepository workItemRepository;
    private final ProjectRepository projectRepository;
    private final Clock clock;

    public WorkApplicationService(WorkItemRepository workItemRepository,
                                  ProjectRepository projectRepository,
                                  Clock clock) {
        this.workItemRepository = workItemRepository;
        this.projectRepository = projectRepository;
        this.clock = clock;
    }

    public WorkItem create(String title, String description, UUID projectId) {
        requireProject(projectId);
        return workItemRepository.save(WorkItem.create(title, description, projectId, Instant.now(clock)));
    }

    @Transactional(readOnly = true)
    public List<WorkItem> list() {
        return workItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkItem get(UUID id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work item not found: " + id));
    }

    public WorkItem associateProject(UUID id, UUID projectId) {
        requireProject(projectId);
        WorkItem item = get(id);
        item.associateProject(projectId, Instant.now(clock));
        return workItemRepository.save(item);
    }

    public WorkItem start(UUID id) {
        WorkItem item = get(id);
        item.start(Instant.now(clock));
        return workItemRepository.save(item);
    }

    public WorkItem complete(UUID id) {
        WorkItem item = get(id);
        item.complete(Instant.now(clock));
        return workItemRepository.save(item);
    }

    private void requireProject(UUID projectId) {
        if (projectId != null && projectRepository.findById(projectId).isEmpty()) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
    }
}
