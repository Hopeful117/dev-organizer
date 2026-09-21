package com.developeros.organizer.application;

import com.developeros.organizer.domain.InboxItem;
import com.developeros.organizer.domain.InboxItemRepository;
import com.developeros.organizer.domain.InboxItemStatus;
import com.developeros.organizer.domain.Project;
import com.developeros.organizer.domain.ProjectRepository;
import com.developeros.organizer.domain.WorkItem;
import com.developeros.organizer.domain.WorkItemRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrganizerApplicationServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-21T20:00:00Z");

    private InMemoryProjects projects;
    private InMemoryInbox inbox;
    private InMemoryWork work;
    private InboxApplicationService inboxService;
    private WorkApplicationService workService;

    @BeforeEach
    void setUp() {
        projects = new InMemoryProjects();
        inbox = new InMemoryInbox();
        work = new InMemoryWork();
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        inboxService = new InboxApplicationService(inbox, work, projects, clock);
        workService = new WorkApplicationService(work, projects, clock);
    }

    @Test
    void capturesDismissesAndListsOnlyCapturedItems() {
        InboxItem item = inboxService.capture("Review architecture", null);
        inboxService.dismiss(item.id());

        assertThat(inboxService.listCaptured()).isEmpty();
        assertThat(inbox.findById(item.id()).orElseThrow().status()).isEqualTo(InboxItemStatus.DISMISSED);
    }

    @Test
    void promotionPreservesProjectAndRejectsRepeatedPromotion() {
        Project project = Project.create("Organizer", NOW);
        projects.save(project);
        InboxItem item = inboxService.capture("Build API", project.id());

        WorkItem promoted = inboxService.promoteToWork(item.id());

        assertThat(promoted.projectId()).isEqualTo(project.id());
        assertThat(promoted.title()).isEqualTo("Build API");
        assertThat(inbox.findById(item.id()).orElseThrow().status()).isEqualTo(InboxItemStatus.PROMOTED);
        assertThatThrownBy(() -> inboxService.promoteToWork(item.id()))
                .isInstanceOf(com.developeros.organizer.domain.DomainException.class);
        assertThat(work.items).hasSize(1);
    }

    @Test
    void workServiceEnforcesLifecycle() {
        WorkItem item = workService.create("Implement tests", null, null);

        workService.start(item.id());
        workService.complete(item.id());

        assertThat(workService.get(item.id()).status()).isEqualTo(com.developeros.organizer.domain.WorkItemStatus.DONE);
        assertThatThrownBy(() -> workService.complete(item.id()))
                .isInstanceOf(com.developeros.organizer.domain.DomainException.class);
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

    private static final class InMemoryInbox implements InboxItemRepository {
        private final List<InboxItem> items = new ArrayList<>();

        @Override
        public InboxItem save(InboxItem item) {
            items.removeIf(existing -> existing.id().equals(item.id()));
            items.add(item);
            return item;
        }

        @Override
        public Optional<InboxItem> findById(UUID id) {
            return items.stream().filter(item -> item.id().equals(id)).findFirst();
        }

        @Override
        public Optional<InboxItem> findByIdForUpdate(UUID id) {
            return findById(id);
        }

        @Override
        public List<InboxItem> findByStatus(InboxItemStatus status) {
            return items.stream().filter(item -> item.status() == status).toList();
        }
    }

    private static final class InMemoryWork implements WorkItemRepository {
        private final List<WorkItem> items = new ArrayList<>();

        @Override
        public WorkItem save(WorkItem item) {
            items.removeIf(existing -> existing.id().equals(item.id()));
            items.add(item);
            return item;
        }

        @Override
        public Optional<WorkItem> findById(UUID id) {
            return items.stream().filter(item -> item.id().equals(id)).findFirst();
        }

        @Override
        public List<WorkItem> findAll() {
            return List.copyOf(items);
        }
    }
}
