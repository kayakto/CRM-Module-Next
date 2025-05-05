package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateRobotRequest(
        @NotBlank String name,
        @NotBlank String message,
        String link
) {}

