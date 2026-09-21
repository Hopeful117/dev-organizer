package com.developeros.organizer.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkItemRepository {
    WorkItem save(WorkItem item);
    Optional<WorkItem> findById(UUID id);
    List<WorkItem> findAll();
}
