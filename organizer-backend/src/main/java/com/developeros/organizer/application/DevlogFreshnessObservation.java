package com.developeros.organizer.application;

import com.developeros.organizer.domain.DevlogProjectReference;

import java.time.Instant;
import java.util.UUID;

public record DevlogFreshnessObservation(
        DevlogProjectReference project,
        UUID sourceId,
        String status,
        String guidance,
        Instant observedAt,
        String currentRevision,
        String ingestedRevision,
        String baselineRevision,
        String sourceReference
) { }
