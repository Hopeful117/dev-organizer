package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.InboxItemStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InboxItemSpringDataRepository extends JpaRepository<InboxItemJpaEntity, UUID> {
    List<InboxItemJpaEntity> findByStatus(InboxItemStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from InboxItemJpaEntity item where item.id = :id")
    java.util.Optional<InboxItemJpaEntity> findByIdForUpdate(@Param("id") UUID id);
}
