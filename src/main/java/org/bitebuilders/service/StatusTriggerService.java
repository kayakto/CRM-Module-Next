package org.bitebuilders.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.StatusTrigger;
import org.bitebuilders.model.Trigger;
import org.bitebuilders.repository.StatusTriggerJdbcRepository;
import org.bitebuilders.repository.TriggerJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatusTriggerService {

    private final TriggerJdbcRepository triggerRepository;
    private final StatusTriggerJdbcRepository statusTriggerRepository;

    /**
     * Привязка триггера к статусу с параметрами
     */
    public void linkTriggerToStatus(Long statusId, Long triggerId, Map<String, Object> parameters) {
        // Проверяем, существует ли триггер
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new IllegalArgumentException("Триггер с id " + triggerId + " не найден"));

        // Проверяем, не привязан ли уже этот триггер к статусу
        if (statusTriggerRepository.isLinkedToStatus(statusId, triggerId)) {
            throw new IllegalStateException("Триггер уже привязан к этому статусу");
        }

        validateParameters(trigger.getType(), parameters);

        // Создаем и сохраняем связь
        StatusTrigger statusTrigger = new StatusTrigger();
        statusTrigger.setStatusId(statusId);
        statusTrigger.setTriggerId(triggerId);
        statusTrigger.setParameters(parameters);

        statusTriggerRepository.save(statusTrigger);
    }

    /**
     * Удаление триггера у статуса
     */
    public void unlinkTriggerFromStatus(Long statusId, Long triggerId) {
        // Удаляем статусный триггер
        statusTriggerRepository.delete(statusId, triggerId);

        // Также удалим сам триггер, если он больше не нужен (опционально)
        // triggerRepository.delete(triggerId);
    }

    /**
     * Получение всех триггеров, связанных с конкретным статусом
     */
    public List<Trigger> getTriggersByStatusId(Long statusId) {
        List<StatusTrigger> statusTriggers = statusTriggerRepository.findByStatusId(statusId);
        List<Trigger> result = new ArrayList<>();

        for (StatusTrigger st : statusTriggers) {
            triggerRepository.findById(st.getTriggerId()).ifPresent(trigger -> {
                // Можно дополнительно объединить параметры из status_trigger в TriggerDTO, если понадобится
                result.add(trigger);
            });
        }

        return result;
    }

    /**
     * Обновление параметров триггера у статуса
     */
    public void updateStatusTriggerParameters(Long statusId, Long triggerId, Map<String, Object> newParams) {
        StatusTrigger updated = new StatusTrigger();
        updated.setStatusId(statusId);
        updated.setTriggerId(triggerId);
        updated.setParameters(newParams);
        statusTriggerRepository.update(updated);
    }

    private void validateParameters(String type, Map<String, Object> params) {
        switch (type) {
            case "LINK_CLICK" -> {
                if (!params.containsKey("link") || params.get("link").toString().isBlank()) {
                    throw new IllegalArgumentException("Поле 'link' обязательно");
                }
            }
            case "TEST_RESULT" -> {
                if (!params.containsKey("condition") || !params.containsKey("value")) {
                    throw new IllegalArgumentException("Поля 'condition' и 'value' обязательны");
                }
                Object value = params.get("value");
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException("'value' должно быть числом");
                }
            }
            default -> throw new IllegalArgumentException("Неизвестный тип триггера: " + type);
        }
    }
}

