package com.developeros.organizer.integration.devlog;

import com.developeros.organizer.application.DevlogFreshnessObservation;
import com.developeros.organizer.application.DevlogFreshnessPort;
import com.developeros.organizer.application.DevlogProjectCatalogPort;
import com.developeros.organizer.domain.DevlogProjectReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DevlogRestAdapter implements DevlogFreshnessPort, DevlogProjectCatalogPort {
    private final RestClient client;

    public DevlogRestAdapter(RestClient.Builder builder,
                             @Value("${devlog.base-url:http://127.0.0.1:18080}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    @Override
    public List<DevlogFreshnessObservation> fetch(DevlogProjectReference project) {
        FreshnessSummary response = client.get()
                .uri("/api/v1/projects/{id}/freshness-checks/summary", project.id())
                .retrieve()
                .body(FreshnessSummary.class);
        if (response == null || response.checkedSources() == null) return List.of();
        return response.checkedSources().stream()
                .filter(source -> source.source() != null && source.source().id() != null)
                .map(source -> new DevlogFreshnessObservation(
                        project,
                        source.source().id(),
                        source.status(),
                        source.guidance(),
                        source.checkedAt(),
                        source.source().currentRevision(),
                        source.source().ingestedRevision(),
                        source.baseline() == null ? null : source.baseline().analyzedRevision(),
                        "devlog://projects/" + project.slug() + "/freshness"))
                .toList();
    }

    @Override
    public List<DevlogProjectOption> listProjects() {
        ProjectOption[] response = client.get().uri("/api/v1/projects")
                .retrieve().body(ProjectOption[].class);
        if (response == null) return List.of();
        return java.util.Arrays.stream(response)
                .map(project -> new DevlogProjectOption(project.id(), project.name(), project.slug(), project.status()))
                .toList();
    }

    private record FreshnessSummary(List<FreshnessSource> checkedSources) { }

    private record FreshnessSource(
            Instant checkedAt,
            String status,
            String guidance,
            FreshnessSourceIdentity source,
            FreshnessBaseline baseline
    ) { }

    private record FreshnessSourceIdentity(
            UUID id,
            String name,
            String defaultBranch,
            String requestedRevision,
            String currentRevision,
            String ingestedRevision
    ) { }

    private record FreshnessBaseline(UUID analysisId, Instant completedAt, String analyzedRevision) { }

    private record ProjectOption(UUID id, String name, String slug, String status) { }
}
