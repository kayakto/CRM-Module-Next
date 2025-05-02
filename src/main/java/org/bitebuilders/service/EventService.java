package org.bitebuilders.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.component.UserContext;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.repository.ApplicationStatusJdbcRepository;
import org.bitebuilders.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class EventService {

    /**
     *  репозиторий с готовыми методами для events
     */
    private final EventRepository eventRepository;

    private final UserContext userContext;

    private final UserInfoService userInfoService;

    private final ApplicationStatusJdbcRepository applicationStatusRepository;

    @Autowired
    public EventService(EventRepository eventRepository, UserContext userContext, UserInfoService userInfoService, ApplicationStatusJdbcRepository applicationStatusRepository) {
        this.eventRepository = eventRepository;
        this.userContext = userContext;
        this.userInfoService = userInfoService;
        this.applicationStatusRepository = applicationStatusRepository;
    }

    public void isPresentEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new EventNotFoundException("Event doesn`t exist");
    }

    // Метод, который возвращает все мероприятия со статусом регистрация открыта
    public List<Event> getOpenedEvents() {
        return eventRepository.findAllByStatus(Event.Status.REGISTRATION_OPEN);
    }

    // Метод, который возвращает все мероприятия
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    // Метод, который возвращает все мероприятия, у которых есть переданный admin_id
    public List<Event> getEventsByAdminId(Long adminId) {
        return eventRepository.findAllByAdminId(adminId);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    public List<Event> getEventsMoreEventStartDate(OffsetDateTime dateTime) {
        return eventRepository.findStartedEventsByDate(dateTime);
    }

    public List<Event> getMyEvents() {
        UserInfo user = userContext.getCurrentUser();
        Long userId = user.getId();

        switch (user.getRole_enum()) { // ролей может быть больше
            case ADMIN -> {
                return eventRepository.findAllByAdminId(userId);
            }
        }

        return Collections.emptyList();
    }

    @Transactional
    public Event createOrUpdateEvent(Event event) {
        Event resultEvent;
        // Случай для обновления или создания мероприятия через контроллер
        if (event.getStatus() == null) {
            validateEvent(event);
            resultEvent = updateEventStatus(event);
        } else {
            resultEvent = eventRepository.save(event);
        }

        // После создания нового мероприятия — клонируем глобальные статусы
        if (event.getId() == null) {
            throw new IllegalStateException("Event ID must not be null after save");
        }

        // Проверяем, нет ли уже статусов у этого мероприятия (на случай update)
        List<ApplicationStatus> existing = applicationStatusRepository.findByEventId(resultEvent.getId());
        if (existing.isEmpty()) {
            List<ApplicationStatus> globalStatuses = applicationStatusRepository.findGlobalStatuses();
            List<ApplicationStatus> cloned = globalStatuses.stream()
                    .map(status -> new ApplicationStatus(
                            null,
                            resultEvent.getId(),  // устанавливаем eventId
                            status.getName(),
                            true,
                            status.getDisplayOrder(),
                            OffsetDateTime.now()
                    ))
                    .toList();

            applicationStatusRepository.saveAll(cloned);
        }

        return resultEvent;
    }

    public void deleteAllEvents() {
        eventRepository.deleteAll();
    }

    // Ручное управление статусом (для администратора) - администратор вручную изменяет статус мероприятия (“Скрыто”, “Удалено“)
    @Transactional
    public boolean deleteEvent(Long eventId) {
        Event eventToDelete = getEventById(eventId);
        eventToDelete.setStatus(Event.Status.DELETED);
        eventRepository.save(eventToDelete);
        return true;
    }

    @Transactional
    public Event.Status hideOrFindOutEvent(Long eventId) {
        Event eventToHide = getEventById(eventId);

        if (eventToHide.getStatus() == Event.Status.HIDDEN){
            eventToHide.setStatus(Event.Status.PREPARATION);
            return updateEventStatus(eventToHide).getStatus();
        }

        eventToHide.setStatus(Event.Status.HIDDEN);
        return eventRepository.save(eventToHide).getStatus();
    }

    public Event updateEventStatus(Event event) {
        Event.Status newStatus = calculateStatus(event);
        Event.Status currentStatus = event.getStatus();

        if (newStatus != currentStatus) {
            if (newStatus == Event.Status.IN_PROGRESS) {
                return startEventById(event.getId());
            } else if (newStatus == Event.Status.FINISHED){
                return endEventById(event.getId());
            } else {
                event.setStatus(newStatus);
                return createOrUpdateEvent(event);
            }
        }

        return event;
    }

    private Event.Status calculateStatus(Event event) {
        OffsetDateTime now = OffsetDateTime.now();
        Event.Status currentStatus = event.getStatus();

        // Не меняем статус для удалённых/скрытых мероприятий
        if (currentStatus == Event.Status.HIDDEN || currentStatus == Event.Status.DELETED) {
            return currentStatus;
        }

        if (now.isBefore(event.getEnrollmentStartDate())) {
            return Event.Status.PREPARATION;
        } else if (now.isAfter(event.getEnrollmentStartDate()) && now.isBefore(event.getEnrollmentEndDate())) {
            if (event.getNumberSeatsStudent() > 0) {
                return Event.Status.REGISTRATION_OPEN;
            } else {
                return Event.Status.NO_SEATS;
            }
        } else if (now.isAfter(event.getEnrollmentEndDate()) && now.isBefore(event.getEventStartDate())) {
            return Event.Status.REGISTRATION_CLOSED;
        } else if (now.isAfter(event.getEventStartDate()) && now.isBefore(event.getEventEndDate())) {
            return Event.Status.IN_PROGRESS;
        } else if (now.isAfter(event.getEventEndDate())) {
            return Event.Status.FINISHED; // TODO логика завершения
        }

        return currentStatus;
    }

    public Event startEventById(Long eventId) {
        Event eventToStart = getEventToStart(eventId);

        eventToStart.setStatusToStarted();
        System.out.println("Event with ID " + eventToStart.getId() + " is now in progress.");

        // Сохранение изменений в мероприятии
        return createOrUpdateEvent(eventToStart);
    }

    public Event endEventById(Long eventId) {
        Event eventToEnd = getEventById(eventId);
        if (eventToEnd.getStatus() != Event.Status.IN_PROGRESS) {
            throw new IllegalStateException("Event cannot be ended because it is not in progress");
        }


//        for (EventStudent student:studentToEnd) {
//            student.setStudentStatus(StatusRequest.ENDED_EVENT);
//            eventStudentService.save(student);
//        }
//
//        // TODO если сделать контроллер на окончание меро - меняем даты в меро
        eventToEnd.setStatus(Event.Status.FINISHED);
        return createOrUpdateEvent(eventToEnd);
    }

    private Event getEventToStart(Long eventId){
        Event eventToStart = getEventById(eventId);
        Event.Status currentStatus = eventToStart.getStatus();

        // Разрешённые статусы для старта мероприятия
        List<Event.Status> canStart = Arrays.asList(
                Event.Status.REGISTRATION_OPEN,
                Event.Status.NO_SEATS,
                Event.Status.REGISTRATION_CLOSED,
                Event.Status.HIDDEN // костыль
        );

        // Проверка, может ли мероприятие быть запущено
        if (!canStart.contains(currentStatus)) {
            throw new IllegalStateException("Event cannot be started due to its current condition: " + currentStatus);
        }
        return eventToStart;
    }

    public boolean haveAdminAccess(Long eventId) {
        UserInfo user = userContext.getCurrentUser();
        Event event = getEventById(eventId);
        if (!Objects.equals(event.getAdminId(), user.getId())) {
            throw new AccessDeniedException("This admin does not have permission to this event");
        }
        return true;
    }

    public boolean validateEvent(Event event) {
        if (!userInfoService.isAdmin(event.getAdminId())) {
            throw new IllegalArgumentException("Invalid adminId: Administrator does not exist.");
        }

        if (event.getEventStartDate().isAfter(event.getEventEndDate())
                || event.getEventStartDate().isEqual(event.getEventEndDate())) {
            throw new IllegalArgumentException("Event start date must be before event end date.");
        }
        if (event.getEnrollmentStartDate().isAfter(event.getEnrollmentEndDate())
                || event.getEnrollmentStartDate().isEqual(event.getEnrollmentEndDate())) {
            throw new IllegalArgumentException("Enrollment start date must be before enrollment end date.");
        }
        if (event.getEnrollmentEndDate().isAfter(event.getEventStartDate())) {
            throw new IllegalArgumentException("Enrollment end date must be before event start date.");
        }

        if (event.getNumberSeatsStudent() <= 0) {
            throw new IllegalArgumentException("Number of seats for students must be greater than zero.");
        }

        return true;
    }


    public Event getEventWithStatuses(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Загружаем кастомные статусы (не системные)
        List<ApplicationStatus> customStatuses = applicationStatusRepository.findByEventId(eventId).stream()
                .filter(status -> !status.isSystemStatus())
                .toList();

        event.setCustomStatuses(customStatuses);
        return event;
    }

    public void addStatusToEvent(Long eventId, ApplicationStatus status) {
        status.setEventId(eventId);
        applicationStatusRepository.save(status);
    }
}
