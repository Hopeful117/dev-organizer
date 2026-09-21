package com.developeros.organizer.persistence;

import com.developeros.organizer.domain.AttentionSource;
import com.developeros.organizer.domain.AttentionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttentionSpringDataRepository extends JpaRepository<AttentionJpaEntity, UUID> {
    Optional<AttentionJpaEntity> findByProjectIdAndSourceAndSourceKey(
            UUID projectId, AttentionSource source, String sourceKey);
    List<AttentionJpaEntity> findByStateInOrderByObservedAtDesc(
            List<AttentionState> states);
    List<AttentionJpaEntity> findByProjectIdAndSource(UUID projectId, AttentionSource source);
}
