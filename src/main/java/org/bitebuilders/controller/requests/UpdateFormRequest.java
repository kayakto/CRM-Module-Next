package org.bitebuilders.controller.requests;

import java.util.List;

public record UpdateFormRequest(
        String title,
        List<FieldRequest> fields
) {}
