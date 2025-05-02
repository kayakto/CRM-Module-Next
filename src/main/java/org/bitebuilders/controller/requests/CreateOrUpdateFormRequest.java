package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateOrUpdateFormRequest(
        @NotNull Long eventId,
        @NotBlank String title,
        Boolean isTemplate,
        Map<Long, FieldSettings> customFields, // key: fieldId, value: настройки поля
        Map<Long, Integer> systemFields // key: fieldId, value: displayOrder
) {
    public record FieldSettings(
            boolean isRequired,
            int displayOrder
    ) {}
}
