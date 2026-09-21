package com.developeros.organizer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record WorkCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String description,
        UUID projectId
) {
}
