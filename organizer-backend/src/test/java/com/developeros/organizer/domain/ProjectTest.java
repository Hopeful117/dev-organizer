package com.developeros.organizer.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    private final Instant now = Instant.parse("2026-09-21T20:00:00Z");

    @Test
    void createsActiveProject() {
        Project project = Project.create("Organizer", now);

        assertThat(project.id()).isNotNull();
        assertThat(project.name()).isEqualTo("Organizer");
        assertThat(project.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(project.createdAt()).isEqualTo(now);
    }

    @Test
    void archivesProject() {
        Project project = Project.create("Organizer", now);

        project.archive(now.plusSeconds(1));

        assertThat(project.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(project.updatedAt()).isEqualTo(now.plusSeconds(1));
    }
}
