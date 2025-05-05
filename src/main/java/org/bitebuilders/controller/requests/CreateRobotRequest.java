package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateRobotRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String message,
        String link
) {}
