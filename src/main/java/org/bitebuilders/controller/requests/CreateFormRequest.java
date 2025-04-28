package org.bitebuilders.controller.requests;

import java.util.List;

public record CreateFormRequest(
        Long eventId,
        String title,
        List<Long> selectedFieldIds,
        Boolean isTemplate
) {}
