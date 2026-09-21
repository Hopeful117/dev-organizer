package com.developeros.organizer.application;

import com.developeros.organizer.domain.AttentionItem;
import com.developeros.organizer.domain.AttentionRepository;
import com.developeros.organizer.domain.AttentionSource;
import com.developeros.organizer.domain.AttentionState;
import com.developeros.organizer.domain.DevlogProjectReference;
import com.developeros.organizer.domain.Project;
import com.developeros.organizer.domain.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AttentionApplicationServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-21T20:00:00Z");

    private InMemoryProjects projects;
    private InMemoryAttentions attentions;
    private Project project;
    private UUID sourceId;

    @BeforeEach
    void setUp() {
        projects = new InMemoryProjects();
        attentions = new InMemoryAttentions();
        project = Project.create("Organizer", NOW);
        project.linkDevlogProject(new DevlogProjectReference(UUID.randomUUID(), "dev-organizer"), NOW);
        projects.save(project);
        sourceId = UUID.randomUUID();
    }

    @Test
    void deduplicatesRepeatedObservationAndHealthyObservationResolvesIt() {
        DevlogFreshnessPort freshness = ignored -> List.of(observation("STALE", "Refresh recommended"));
        AttentionApplicationService service = service(freshness);

        service.refreshAll();
        service.refreshAll();

        assertThat(attentions.items).hasSize(1);
        UUID attentionId = attentions.items.getFirst().id();
        service.acknowledge(attentionId);
        assertThat(attentions.items.getFirst().state()).isEqualTo(AttentionState.ACKNOWLEDGED);

        freshness = ignored -> List.of(observation("CURRENT", "Healthy"));
        service = service(freshness);
        service.refreshAll();

        assertThat(attentions.items.getFirst().state()).isEqualTo(AttentionState.RESOLVED);
        assertThat(service.listCurrent()).isEmpty();
    }

    @Test
    void failedProjectDoesNotPreventOtherProjectsFromRefreshing() {
        Project second = Project.create("Second", NOW);
        second.linkDevlogProject(new DevlogProjectReference(UUID.randomUUID(), "second"), NOW);
        projects.save(second);

        AttentionApplicationService service = service(reference -> {
            if (reference.slug().equals("dev-organizer")) throw new IllegalStateException("connection refused");
            return List.of(observationFor(reference, "NO_BASELINE", "No baseline"));
        });

        AttentionRefreshResult result = service.refreshAll();

        assertThat(result.errors()).singleElement().extracting(AttentionRefreshResult.IntegrationError::projectId)
                .isEqualTo(project.id());
        assertThat(result.items()).singleElement().satisfies(item -> {
            assertThat(item.projectId()).isEqualTo(second.id());
            assertThat(item.source()).isEqualTo(AttentionSource.DEVLOG);
        });
    }

    private AttentionApplicationService service(DevlogFreshnessPort freshness) {
        return new AttentionApplicationService(attentions, projects, freshness, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private DevlogFreshnessObservation observation(String status, String guidance) {
        return observationFor(project.devlogProject(), status, guidance);
    }

    private DevlogFreshnessObservation observationFor(DevlogProjectReference reference, String status, String guidance) {
        return new DevlogFreshnessObservation(reference, sourceId, status, guidance, NOW,
                "current", "ingested", "baseline", "http://localhost:8181/projects/" + reference.slug());
    }

    private static final class InMemoryProjects implements ProjectRepository {
        private final List<Project> items = new ArrayList<>();

        @Override
        public Project save(Project project) {
            items.removeIf(existing -> existing.id().equals(project.id()));
            items.add(project);
            return project;
        }

        @Override
        public Optional<Project> findById(UUID id) {
            return items.stream().filter(item -> item.id().equals(id)).findFirst();
        }

        @Override
        public List<Project> findAll() {
            return List.copyOf(items);
        }
    }

    private static final class InMemoryAttentions implements AttentionRepository {
        private final List<AttentionItem> items = new ArrayList<>();

        @Override
        public AttentionItem save(AttentionItem item) {
            items.removeIf(existing -> existing.id().equals(item.id()));
            items.add(item);
            return item;
        }

        @Override
        public Optional<AttentionItem> findById(UUID id) {
            return items.stream().filter(item -> item.id().equals(id)).findFirst();
        }

        @Override
        public Optional<AttentionItem> findByProjectAndSourceKey(UUID projectId, AttentionSource source, String sourceKey) {
            return items.stream().filter(item -> item.projectId().equals(projectId)
                    && item.source() == source && item.sourceKey().equals(sourceKey)).findFirst();
        }

        @Override
        public List<AttentionItem> findCurrent() {
            return items.stream().filter(item -> item.state() == AttentionState.OPEN
                    || item.state() == AttentionState.ACKNOWLEDGED).toList();
        }

        @Override
        public List<AttentionItem> findByProjectAndSource(UUID projectId, AttentionSource source) {
            return items.stream().filter(item -> item.projectId().equals(projectId) && item.source() == source).toList();
        }
    }
}
