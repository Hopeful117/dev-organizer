package com.developeros.organizer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InboxCaptureRequest(
        @NotBlank @Size(max = 10000) String content,
        UUID projectId
) {
}
