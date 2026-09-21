package com.developeros.organizer.domain;

import java.util.Objects;
import java.util.UUID;

public record DevlogProjectReference(UUID id, String slug) {
    public DevlogProjectReference {
        Objects.requireNonNull(id, "DevLog project id must not be null");
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("DevLog project slug must not be blank");
        }
        slug = slug.trim();
    }
}
