package org.bitebuilders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitebuilders.controller.dto.FormDTO;
import org.bitebuilders.controller.requests.CreateOrUpdateFormRequest;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventFormService {
    private final EventFormRepository formRepository;
    private final FormFieldJdbcDao formFieldJdbcDao;
    private final SystemFieldJdbcDao systemFieldJdbcDao;
    private final FormSystemFieldJdbcDao formSystemFieldJdbcDao;
    private final StandardFieldJdbcDao standardFieldJdbcDao;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public FormDTO createOrUpdateForm(CreateOrUpdateFormRequest request) {
        // 1. Проверяем существование мероприятия
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new CustomNotFoundException("Мероприятие не найдено"));

        // 2. Проверяем/создаем основную форму
        EventForm form = formRepository.findByEventId(request.eventId())
                .orElseGet(() -> {
                    EventForm newForm = new EventForm();
                    newForm.setEventId(request.eventId());
                    newForm.setCreatedAt(OffsetDateTime.now());
                    return newForm;
                });

        form.setTitle(request.title());
        form.setIsTemplate(request.isTemplate() != null ? request.isTemplate() : false);
        form.setUpdatedAt(OffsetDateTime.now());

        EventForm savedForm = formRepository.save(form);

        // 3. Обрабатываем системные поля
        processSystemFields(savedForm.getId(), request.systemFields());

        // 4. Обрабатываем кастомные поля
        processCustomFields(savedForm.getId(), request.customFields());

        return getFormByEventId(savedForm.getEventId());
    }

    private void processSystemFields(Long formId, Map<Long, Integer> systemFields) {
        // 1. Валидация системных полей
        List<SystemField> allSystemFields = systemFieldJdbcDao.findAll();
        if (systemFields.size() != allSystemFields.size()) {
            throw new ValidationException("Должны быть указаны все системные поля");
        }

        // 2. Удаляем старые привязки
        formSystemFieldJdbcDao.deleteByFormId(formId);

        // 3. Сохраняем новые привязки
        systemFields.forEach((fieldId, displayOrder) -> {
            if (!allSystemFields.stream().anyMatch(f -> f.getId().equals(fieldId))) {
                throw new ValidationException("Системное поле с ID " + fieldId + " не существует");
            }

            FormSystemField formSystemField = new FormSystemField();
            formSystemField.setFormId(formId);
            formSystemField.setSystemFieldId(fieldId);
            formSystemField.setDisplayOrder(displayOrder);
            formSystemField.setIsRequired(true); // Системные поля всегда обязательные

            formSystemFieldJdbcDao.insert(formSystemField);
        });
    }

    private void processCustomFields(Long formId, Map<Long, CreateOrUpdateFormRequest.FieldSettings> customFields) {
        // 1. Удаляем старые кастомные поля
        formFieldJdbcDao.deleteByFormId(formId);

        if (customFields != null && !customFields.isEmpty()) {
            // 2. Проверяем существование полей
            List<Long> fieldIds = new ArrayList<>(customFields.keySet());
            List<StandardField> existingFields = standardFieldJdbcDao.findByIds(fieldIds);

            if (existingFields.size() != fieldIds.size()) {
                throw new CustomNotFoundException("Некоторые стандартные поля не найдены");
            }

            // 3. Сохраняем новые поля
            customFields.forEach((fieldId, settings) -> {
                StandardField standardField = existingFields.stream()
                        .filter(f -> f.getId().equals(fieldId))
                        .findFirst()
                        .orElseThrow();

                FormField formField = new FormField();
                formField.setFormId(formId);
                formField.setName(standardField.getName());
                formField.setType(standardField.getType());
                formField.setIsRequired(settings.isRequired());
                formField.setDisplayOrder(settings.displayOrder());
                formField.setOptions(standardField.getOptions());
                formField.setOptionsJson(convertOptionsToJson(standardField.getOptions()));

                formFieldJdbcDao.insertField(formField);
            });
        }
    }

    public FormDTO getFormByEventId(Long eventId) {
        EventForm form = formRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomNotFoundException("Форма для мероприятия не найдена"));

        // 1. Получаем системные поля формы
        List<SystemField> systemFields = systemFieldJdbcDao.findByFormId(form.getId());
        Map<Long, Integer> systemFieldOrders = formSystemFieldJdbcDao.findDisplayOrdersForForm(form.getId());

        // 2. Получаем кастомные поля формы
        List<FormField> customFields = formFieldJdbcDao.findByFormIdOrderByDisplayOrder(form.getId());

        return new FormDTO(
                form.getId(),
                form.getEventId(),
                form.getTitle(),
                form.getIsTemplate(),
                systemFields.stream()
                        .map(f -> new FormDTO.FieldInfo(
                                f.getId(),
                                f.getName(),
                                f.getType(),
                                formSystemFieldJdbcDao.isSystemFieldRequired(form.getId(), f.getId()),
                                systemFieldOrders.get(f.getId()),
                                Collections.emptyList()
                        ))
                        .sorted(Comparator.comparingInt(FormDTO.FieldInfo::displayOrder))
                        .toList(),
                customFields.stream()
                        .map(f -> new FormDTO.FieldInfo(
                                f.getId(),
                                f.getName(),
                                f.getType(),
                                f.getIsRequired(),
                                f.getDisplayOrder(),
                                f.getOptions()
                        ))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<FormDTO> getAllForms() {
        List<EventForm> eventForms = (List<EventForm>) formRepository.findAll();
        return eventForms.stream()
                .map(form -> getFormByEventId(form.getEventId()))
                .toList();
    }

    @Transactional
    public void deleteForm(Long eventId) {
        // Сначала находим форму, чтобы получить ее ID
        EventForm form = formRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomNotFoundException("Форма для мероприятия не найдена"));

        // 1. Удаляем привязки системных полей
        formSystemFieldJdbcDao.deleteByFormId(form.getId());

        // 2. Удаляем кастомные поля
        formFieldJdbcDao.deleteByFormId(form.getId());

        // 3. Удаляем саму форму
        formRepository.deleteById(form.getId());
    }

    @Transactional(readOnly = true)
    public void validateApplicationData(Long eventId, Map<String, Object> formData) {
        FormDTO form = getFormByEventId(eventId);

        // 1. Проверяем системные поля
        form.systemFields().forEach(field -> {
            if (!formData.containsKey(field.name()) || formData.get(field.name()) == null) {
                throw new ValidationException("Не заполнено обязательное поле: " + field.name());
            }
        });

        // 2. Проверяем кастомные поля
        form.customFields().stream()
                .filter(FormDTO.FieldInfo::isRequired)
                .forEach(field -> {
                    if (!formData.containsKey(field.name()) || formData.get(field.name()) == null) {
                        throw new ValidationException("Не заполнено обязательное поле: " + field.name());
                    }
                });

        // 3. Дополнительные проверки
        if (formData.containsKey("email") && !isValidEmail(formData.get("email").toString())) {
            throw new ValidationException("Неверный формат email");
        }
    }

    public List<SystemField> getAllSystemFields() {
        return systemFieldJdbcDao.findAll()
                .stream()
                .sorted(Comparator.comparingInt(SystemField::getDisplayOrder))
                .toList();
    }

    public List<StandardField> getAllStandardFields() {
        return standardFieldJdbcDao.findAll()
                .stream()
                .sorted(Comparator.comparingInt(StandardField::getDisplayOrder))
                .toList();
    }

    private String convertOptionsToJson(List<String> options) {
        try {
            return objectMapper.writeValueAsString(options != null ? options : List.of());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize options", e);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
