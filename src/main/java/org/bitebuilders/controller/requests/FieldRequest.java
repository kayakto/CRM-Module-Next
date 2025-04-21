package org.bitebuilders.controller.requests;

import org.bitebuilders.model.FormField;

import java.util.List;

public record FieldRequest(
        String name,
        String type,
        boolean required,
        Integer displayOrder,
        List<String> options
) {
    public FormField toFormField() {
        return new FormField(name, type, required, displayOrder, options);
    }
}
