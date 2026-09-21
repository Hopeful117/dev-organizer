package com.developeros.organizer.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InboxItemRepository {
    InboxItem save(InboxItem item);
    Optional<InboxItem> findById(UUID id);
    Optional<InboxItem> findByIdForUpdate(UUID id);
    List<InboxItem> findByStatus(InboxItemStatus status);
}
