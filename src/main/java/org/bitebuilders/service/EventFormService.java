package org.bitebuilders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.model.EventForm;
import org.bitebuilders.model.FormField;
import org.bitebuilders.repository.EventFormRepository;
import org.bitebuilders.repository.FormFieldJdbcDao;
import org.bitebuilders.repository.FormFieldRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class EventFormService {
    private final EventFormRepository formRepository;
    private final FormFieldRepository fieldRepository;
    private final FormFieldJdbcDao formFieldJdbcDao;
    private final ObjectMapper objectMapper; // Для работы с JSON

    public EventFormService(EventFormRepository formRepository, FormFieldRepository fieldRepository, FormFieldJdbcDao formFieldJdbcDao, ObjectMapper objectMapper) {
        this.formRepository = formRepository;
        this.fieldRepository = fieldRepository;
        this.formFieldJdbcDao = formFieldJdbcDao;
        this.objectMapper = objectMapper;
    }

    public EventForm createForm(Long eventId, String title, List<FormField> fields) {
        EventForm form = new EventForm();
        form.setEventId(eventId);
        form.setTitle(title);
        form.setCreatedAt(OffsetDateTime.now());

        EventForm savedForm = formRepository.save(form);

        for (FormField field : fields) {
            field.setFormId(savedForm.getId());
            formFieldJdbcDao.insertField(field);
        }

        return savedForm;
    }

    public EventForm updateForm(Long formId, String title, List<FormField> fields) {
        EventForm form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        form.setTitle(title);
        form.setUpdatedAt(OffsetDateTime.now());

        formFieldJdbcDao.deleteByFormId(formId);

        for (FormField field : fields) {
            field.setFormId(formId);
            formFieldJdbcDao.insertField(field);
        }

        return formRepository.save(form);
    }

    // Удаление анкеты
    public void deleteForm(Long formId) {
        // Каскадное удаление полей благодаря ON DELETE CASCADE
        formRepository.deleteById(formId);
    }

    // Получение всех анкет
    public List<EventForm> getAllForms() {
        return (List<EventForm>) formRepository.findAll();
    }

    // Получение анкеты с полями
    public EventForm getFormWithFields(Long formId) {
        EventForm form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        List<FormField> fields = fieldRepository.findByFormIdOrderByDisplayOrder(formId);
        // Здесь можно добавить fields в DTO, если используете
        return form;
    }

    private String convertOptionsToJson(List<String> options) {
        if (options == null) return null;
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert options to JSON", e);
        }
    }

    private List<String> convertJsonToOptions(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse options JSON", e);
        }
    }

}
