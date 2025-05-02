package org.bitebuilders.controller.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApplicationDTO(
        Long id,
        Long eventId,
        Long statusId,
        Map<String, Object> formData,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}