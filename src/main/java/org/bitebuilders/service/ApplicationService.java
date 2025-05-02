package org.bitebuilders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.bitebuilders.controller.dto.ApplicationDTO;
import org.bitebuilders.controller.dto.FormDTO;
import org.bitebuilders.controller.requests.CreateApplicationRequest;
import org.bitebuilders.controller.requests.UpdateApplicationStatusRequest;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.exception.DuplicateApplicationException;
import org.bitebuilders.model.Application;
import org.bitebuilders.model.ApplicationStatusHistory;
import org.bitebuilders.model.EventForm;
import org.bitebuilders.repository.ApplicationJdbcRepository;
import org.bitebuilders.repository.ApplicationStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationJdbcRepository applicationRepository;
    private final EventFormService formService;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationJdbcRepository applicationJdbcRepository;

    @Transactional
    public ApplicationDTO createApplication(CreateApplicationRequest request) {
        // Получаем форму мероприятия в виде DTO
        FormDTO form = formService.getFormByEventId(request.eventId());

        // Валидируем данные формы: обязательные поля должны быть заполнены
        validateApplicationData(form, request.formData());

        // Извлекаем email
        String email = extractEmailFromFormData(request.formData());

        // Проверяем уникальность заявки по eventId + email
        if (applicationRepository.existsByEventIdAndFormDataEmail(request.eventId(), email)) {
            throw new DuplicateApplicationException();
        }

        // Сохраняем заявку
        Application application = new Application();
        application.setEventId(request.eventId());
        Long sendStatusId = applicationRepository.
                findStatusByNameAndEventId(request.eventId(), "Отправил(а) заявку");
        application.setStatusId(sendStatusId); // "Отправлено"
        application.setFormData(convertFormDataToString(request.formData()));
        application.setCreatedAt(OffsetDateTime.now());
        application.setUpdatedAt(OffsetDateTime.now());

        Application saved = applicationJdbcRepository.save(application);

        // Сохраняем первую запись в историю статусов
        createStatusHistory(saved.getId(), null, 1L);

        return mapToResponse(saved);
    }


    private String extractEmailFromFormData(Map<String, Object> formData) {
        Object value = formData.get("email");
        if (value == null || !(value instanceof String email) || email.isBlank()) {
            throw new IllegalArgumentException("Поле 'email' обязательно для заполнения");
        }
        return email;
    }

    private void validateApplicationData(FormDTO form, Map<String, Object> formData) {
        List<String> missingFields = new ArrayList<>();

        // Проверяем системные поля
        for (FormDTO.FieldInfo field : form.systemFields()) {
            if (field.isRequired() && !formData.containsKey(field.name())) {
                missingFields.add(field.name());
            }
        }

        // Проверяем кастомные поля
        for (FormDTO.FieldInfo field : form.customFields()) {
            if (field.isRequired() && !formData.containsKey(field.name())) {
                missingFields.add(field.name());
            }
        }

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Не заполнены обязательные поля: " + String.join(", ", missingFields));
        }
    }

    public List<ApplicationDTO> getApplications(Long eventId, Long statusId) {
        List<Application> applications;
        if (eventId != null && statusId != null) {
            applications = applicationRepository.findByEventIdAndStatusId(eventId, statusId);
        } else if (eventId != null) {
            applications = applicationRepository.findByEventId(eventId);
        } else if (statusId != null) {
            applications = applicationRepository.findByStatusId(statusId);
        } else {
            applications = (List<Application>) applicationRepository.findAll();
        }

        return applications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationDTO> getApplicationsByEvent(Long eventId) {
        List<Application> applications = applicationRepository.findByEventId(eventId);
        return applications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ApplicationDTO updateStatus(Long id, Long newStatusId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Заявка не найдена"));

        Long oldStatusId = application.getStatusId();
        application.setStatusId(newStatusId);
        application.setUpdatedAt(OffsetDateTime.now());

        Application updatedApplication = applicationRepository.save(application);
        createStatusHistory(id, oldStatusId, newStatusId);

        return mapToResponse(updatedApplication);
    }

    private ApplicationDTO mapToResponse(Application application) {
        return new ApplicationDTO(
                application.getId(),
                application.getEventId(),
                application.getStatusId(),
                convertStringToFormData(application.getFormData()),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private String convertFormDataToString(Map<String, Object> formData) {
        try {
            return new ObjectMapper().writeValueAsString(formData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации данных формы", e);
        }
    }


    private Map<String, Object> convertStringToFormData(String formData) {
        try {
            return objectMapper.readValue(formData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new ValidationException("Ошибка чтения данных формы", e);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private void createStatusHistory(Long applicationId, Long fromStatusId, Long toStatusId) {
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setApplicationId(applicationId);
        history.setFromStatusId(fromStatusId);
        history.setToStatusId(toStatusId);
        history.setChangedAt(OffsetDateTime.now());
        historyRepository.save(history);
    }

    public ApplicationDTO getApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Заявка не найдена"));
        return mapToResponse(application);
    }
}
