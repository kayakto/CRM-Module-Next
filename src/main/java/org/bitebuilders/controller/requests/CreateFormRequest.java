package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record CreateFormRequest(
        @NotNull Long eventId,
        @NotBlank String title,
        Boolean isTemplate,
        List<Long> customFieldIds,
        @NotNull
        @Size(min = 6, message = "Must specify all system fields")
        Map<Long, Boolean> systemFields
) {}
