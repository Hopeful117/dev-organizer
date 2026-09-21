package com.developeros.organizer.application;

import com.developeros.organizer.domain.AttentionItem;
import com.developeros.organizer.domain.AttentionRepository;
import com.developeros.organizer.domain.AttentionSource;
import com.developeros.organizer.domain.AttentionState;
import com.developeros.organizer.domain.Project;
import com.developeros.organizer.domain.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AttentionApplicationService {
    private static final Set<String> REVIEW_STATUSES = Set.of("STALE", "PARTIALLY_FRESH", "NO_BASELINE", "UNKNOWN");
    private static final String HEALTHY_STATUS = "CURRENT";

    private final AttentionRepository attentionRepository;
    private final ProjectRepository projectRepository;
    private final DevlogFreshnessPort freshnessPort;
    private final Clock clock;

    public AttentionApplicationService(AttentionRepository attentionRepository,
                                       ProjectRepository projectRepository,
                                       DevlogFreshnessPort freshnessPort,
                                       Clock clock) {
        this.attentionRepository = attentionRepository;
        this.projectRepository = projectRepository;
        this.freshnessPort = freshnessPort;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<AttentionItem> listCurrent() {
        return attentionRepository.findCurrent();
    }

    public AttentionRefreshResult refreshAll() {
        List<AttentionRefreshResult.IntegrationError> errors = new ArrayList<>();
        for (Project project : projectRepository.findAll()) {
            if (project.status() != com.developeros.organizer.domain.ProjectStatus.ACTIVE
                    || project.devlogProject() == null) continue;
            try {
                reconcile(project, freshnessPort.fetch(project.devlogProject()));
            } catch (RuntimeException exception) {
                errors.add(new AttentionRefreshResult.IntegrationError(
                        project.id(), safeMessage(exception)));
            }
        }
        return new AttentionRefreshResult(attentionRepository.findCurrent(), errors);
    }

    public AttentionItem acknowledge(UUID id) {
        AttentionItem item = get(id);
        item.acknowledge(Instant.now(clock));
        return attentionRepository.save(item);
    }

    public AttentionItem dismiss(UUID id) {
        AttentionItem item = get(id);
        item.dismiss(Instant.now(clock));
        return attentionRepository.save(item);
    }

    private void reconcile(Project project, List<DevlogFreshnessObservation> observations) {
        if (observations.isEmpty()) return;
        Instant now = Instant.now(clock);
        for (DevlogFreshnessObservation observation : observations) {
            if (REVIEW_STATUSES.contains(observation.status())) {
                reconcileReview(project, observation, now);
            } else if (HEALTHY_STATUS.equals(observation.status())) {
                resolveCurrent(project, observation.sourceId(), now);
            }
        }
    }

    private void reconcileReview(Project project, DevlogFreshnessObservation observation, Instant now) {
        String sourceKey = sourceKey(observation);
        AttentionItem item = attentionRepository.findByProjectAndSourceKey(
                        project.id(), AttentionSource.DEVLOG, sourceKey)
                .orElseGet(() -> AttentionItem.open(project.id(), AttentionSource.DEVLOG, sourceKey,
                        reason(observation.status()), observation.guidance(), observation.observedAt(),
                        observation.sourceReference(), now));
        item.reobserve(reason(observation.status()), observation.guidance(), observation.observedAt(),
                observation.sourceReference(), now);
        attentionRepository.save(item);
    }

    private void resolveCurrent(Project project, UUID sourceId, Instant now) {
        attentionRepository.findByProjectAndSource(project.id(), AttentionSource.DEVLOG).stream()
                .filter(item -> item.sourceKey().contains("|" + sourceId + "|"))
                .filter(item -> item.state() == AttentionState.OPEN || item.state() == AttentionState.ACKNOWLEDGED)
                .forEach(item -> {
                    item.resolve(now);
                    attentionRepository.save(item);
                });
    }

    private AttentionItem get(UUID id) {
        return attentionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attention not found: " + id));
    }

    private String sourceKey(DevlogFreshnessObservation observation) {
        return String.join("|", observation.project().id().toString(), observation.sourceId().toString(),
                observation.status(), observation.guidance(), value(observation.currentRevision()),
                value(observation.ingestedRevision()), value(observation.baselineRevision()));
    }

    private String reason(String status) {
        return switch (status) {
            case "PARTIALLY_FRESH" -> "DevLog understanding is behind synchronized repository state.";
            case "STALE" -> "DevLog understanding may need refreshing.";
            case "NO_BASELINE" -> "DevLog has no understanding baseline for this project.";
            default -> "DevLog freshness needs verification.";
        };
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private String safeMessage(RuntimeException exception) {
        return "DevLog could not be reached.";
    }
}
