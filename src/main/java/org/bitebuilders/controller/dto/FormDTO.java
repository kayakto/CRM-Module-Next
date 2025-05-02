package org.bitebuilders.controller.dto;

import org.bitebuilders.model.EventForm;

import java.util.List;

public record FormDTO(
        Long formId, // Добавляем ID формы
        Long eventId,
        String title,
        Boolean isTemplate,
        List<FieldInfo> systemFields,
        List<FieldInfo> customFields
) {
    public record FieldInfo(
            Long id,
            String name,
            String type,
            boolean isRequired,
            int displayOrder,
            List<String> options
    ) {}

//    public EventForm toForm() {
//        return new EventForm(formId, eventId, title, isTemplate, systemFields, customFields);
//    }
}
