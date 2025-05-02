package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateApplicationRequest(
        @NotNull Long eventId,
        @NotNull
        Map<String, Object> formData
) {}
