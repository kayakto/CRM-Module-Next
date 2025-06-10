package org.bitebuilders.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.ApplicationStatus;
import org.bitebuilders.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationStatusService {
    private final ApplicationStatusJdbcRepository statusRepository;
    private final EventJdbcRepository eventRepository;
    private final ApplicationJdbcRepository applicationRepository;

    @Transactional
    public ApplicationStatus createGlobalStatus(ApplicationStatus status) {
        if (statusRepository.existsByIsSystemAndDisplayOrder(true, status.getDisplayOrder())) {
            throw new IllegalArgumentException("Display order must be unique for system statuses");
        }

        status.setIsSystem(true);
        status.setUpdatedAt(OffsetDateTime.now());
        return statusRepository.save(status);
    }

    @Transactional
    public Optional<ApplicationStatus> updateGlobalStatus(Long id, ApplicationStatus newStatus) {
        return statusRepository.findById(id)
                .filter(ApplicationStatus::getIsSystem)
                .map(existing -> {
                    if (!existing.getDisplayOrder().equals(newStatus.getDisplayOrder())
                            && statusRepository.existsByIsSystemAndDisplayOrder(true, newStatus.getDisplayOrder())) {
                        throw new IllegalArgumentException("Display order must be unique for system statuses");
                    }

                    existing.setName(newStatus.getName());
                    existing.setDisplayOrder(newStatus.getDisplayOrder());
                    existing.setUpdatedAt(OffsetDateTime.now());
                    return statusRepository.save(existing);
                });
    }

    @Transactional
    public void deleteGlobalStatus(Long id) {
        ApplicationStatus status = statusRepository.findById(id)
                .filter(ApplicationStatus::getIsSystem)
                .orElseThrow(() -> new CustomNotFoundException("Global status not found"));

        if (statusRepository.existsByStatusId(id)) {
            throw new IllegalStateException("Cannot delete status with existing applications");
        }

        statusRepository.delete(status);
    }

    public List<ApplicationStatus> getAllGlobalStatuses() {
        return statusRepository.findGlobalStatuses();
    }


    @Transactional
    public ApplicationStatus addStatusToEvent(Long eventId, ApplicationStatus status) {
        // Проверяем существование мероприятия
        if (!eventRepository.existsById(eventId)) {
            throw new CustomNotFoundException("Event not found with id: " + eventId);
        }

        // Проверяем уникальность порядка отображения
        if (statusRepository.existsByEventIdAndDisplayOrder(eventId, status.getDisplayOrder())) {
            throw new IllegalArgumentException("Display order must be unique within event");
        }

        status.setEventId(eventId);
        status.setIsSystem(false);
        status.setUpdatedAt(OffsetDateTime.now());
        return statusRepository.save(status);
    }

    @Transactional
    public ApplicationStatus updateEventStatus(Long eventId, Long statusId, ApplicationStatus newStatus) {
        return statusRepository.findById(statusId)
                .filter(status -> status.getEventId() != null && status.getEventId().equals(eventId))
                .map(existing -> {
                    // Проверка на изменение порядка
                    if (!existing.getDisplayOrder().equals(newStatus.getDisplayOrder()) &&
                            statusRepository.existsByEventIdAndDisplayOrder(eventId, newStatus.getDisplayOrder())) {
                        throw new IllegalArgumentException("Display order must be unique within event");
                    }

                    existing.setName(newStatus.getName());
                    existing.setDisplayOrder(newStatus.getDisplayOrder());
                    existing.setUpdatedAt(OffsetDateTime.now());
                    return statusRepository.save(existing);
                })
                .orElseThrow(() -> new CustomNotFoundException(
                        "Status not found with id: " + statusId + " for event: " + eventId));
    }

    @Transactional
    public void removeStatusFromEvent(Long eventId, Long statusId) {
        ApplicationStatus status = statusRepository.findById(statusId)
                .filter(s -> s.getEventId() != null && s.getEventId().equals(eventId))
                .orElseThrow(() -> new CustomNotFoundException(
                        "Status not found with id: " + statusId + " for event: " + eventId));

        if (applicationRepository.existsByStatusId(statusId)) {
            throw new IllegalStateException("Cannot delete status with existing applications");
        }

        statusRepository.delete(status);
    }

    public List<ApplicationStatus> getStatusesByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new CustomNotFoundException("Event not found with id: " + eventId);
        }
        return statusRepository.findByEventId(eventId);
    }

    @Transactional
    public void updateDisplayOrders(Long eventId, Map<Long, Integer> orders) {
        // Получаем все статусы мероприятия
        List<ApplicationStatus> statuses = statusRepository.findByEventId(eventId);

        // Проверка: все ли переданные id существуют
        Set<Long> existingIds = statuses.stream()
                .map(ApplicationStatus::getId)
                .collect(Collectors.toSet());

        for (Long id : orders.keySet()) {
            if (!existingIds.contains(id)) {
                throw new CustomNotFoundException("Статус с id " + id + " не найден для события " + eventId);
            }
        }

        // Проверка на уникальность displayOrder
        List<Integer> newOrders = new ArrayList<>(orders.values());
        Set<Integer> uniqueOrders = new HashSet<>(newOrders);
        if (newOrders.size() != uniqueOrders.size()) {
            throw new IllegalArgumentException("Порядок отображения должен быть уникален для каждого статуса");
        }

        // Обновляем значения
        for (ApplicationStatus status : statuses) {
            if (orders.containsKey(status.getId())) {
                status.setDisplayOrder(orders.get(status.getId()));
                status.setUpdatedAt(OffsetDateTime.now());
                statusRepository.save(status);
            }
        }
    }

}
