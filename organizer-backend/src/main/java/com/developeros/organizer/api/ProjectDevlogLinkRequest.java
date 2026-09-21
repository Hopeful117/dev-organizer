package com.developeros.organizer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProjectDevlogLinkRequest(
        @NotNull UUID devlogProjectId,
        @NotBlank String devlogProjectSlug
) { }
