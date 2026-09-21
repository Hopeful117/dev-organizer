package com.developeros.organizer.application;

import com.developeros.organizer.domain.InboxItem;
import com.developeros.organizer.domain.InboxItemRepository;
import com.developeros.organizer.domain.InboxItemStatus;
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
public class InboxApplicationService {
    private final InboxItemRepository inboxItemRepository;
    private final WorkItemRepository workItemRepository;
    private final ProjectRepository projectRepository;
    private final Clock clock;

    public InboxApplicationService(InboxItemRepository inboxItemRepository,
                                   WorkItemRepository workItemRepository,
                                   ProjectRepository projectRepository,
                                   Clock clock) {
        this.inboxItemRepository = inboxItemRepository;
        this.workItemRepository = workItemRepository;
        this.projectRepository = projectRepository;
        this.clock = clock;
    }

    public InboxItem capture(String content, UUID projectId) {
        requireProject(projectId);
        return inboxItemRepository.save(InboxItem.capture(content, projectId, Instant.now(clock)));
    }

    @Transactional(readOnly = true)
    public List<InboxItem> listCaptured() {
        return inboxItemRepository.findByStatus(InboxItemStatus.CAPTURED);
    }

    @Transactional(readOnly = true)
    public InboxItem get(UUID id) {
        return inboxItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbox item not found: " + id));
    }

    public InboxItem dismiss(UUID id) {
        InboxItem item = get(id);
        item.dismiss();
        return inboxItemRepository.save(item);
    }

    public InboxItem associateProject(UUID id, UUID projectId) {
        requireProject(projectId);
        InboxItem item = get(id);
        item.associateProject(projectId);
        return inboxItemRepository.save(item);
    }

    public WorkItem promoteToWork(UUID id) {
        InboxItem item = inboxItemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbox item not found: " + id));
        item.promote();
        WorkItem workItem = WorkItem.create(item.content(), null, item.projectId(), Instant.now(clock));
        WorkItem savedWorkItem = workItemRepository.save(workItem);
        inboxItemRepository.save(item);
        return savedWorkItem;
    }

    private void requireProject(UUID projectId) {
        if (projectId != null && projectRepository.findById(projectId).isEmpty()) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
    }
}
