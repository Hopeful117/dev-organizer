package com.developeros.organizer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectSpringDataRepository extends JpaRepository<ProjectJpaEntity, UUID> {
}
