package org.bitebuilders.controller.requests;

import java.util.List;
import java.util.Map;

public record UpdateFormRequest(
        String title,
        Boolean isTemplate,
        List<Long> customFieldIds,
        Map<Long, Boolean> systemFields // systemFieldId -> isRequired
) {}
