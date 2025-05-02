package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventDTO;
import org.bitebuilders.controller.dto.MessageResponseDTO;
import org.bitebuilders.controller.requests.EventRequest;
import org.bitebuilders.controller.requests.StartEventRequest;
import org.bitebuilders.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.bitebuilders.service.EventService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getOpenedEvents() {
        List<EventDTO> activeEvents = eventService.getOpenedEvents()
                .stream()
                .map(Event::toEventDTO)
                .toList();

        if (activeEvents != null)
            return ResponseEntity.ok(activeEvents);  // Возвращаем список активных событий

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDTO>> getMyEvents() {
        List<EventDTO> eventDTOS = eventService.getMyEvents()
                .stream()
                .map(Event::toEventDTO)
                .toList();

        return ResponseEntity.ok(eventDTOS);
    }

    // Получение мероприятия по его id
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventByID(@PathVariable Long eventId) {
        Event foundEvent = eventService.getEventById(eventId);
        return ResponseEntity.ok(foundEvent.toEventDTO());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/post")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventRequest requestedEvent) {
        Event newEvent = requestedEvent.toEvent();
        OffsetDateTime now = OffsetDateTime.now();
        newEvent.setCreatedAt(now);
        newEvent.setUpdatedAt(now);
        EventDTO eventDTO = eventService.
                createOrUpdateEvent(newEvent).toEventDTO();

        if (eventDTO != null)
            return ResponseEntity.ok(eventDTO);

        return ResponseEntity.notFound().build();
    }

    /**
     * Запускает мероприятие досрочно
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/start-event")
    public ResponseEntity<EventDTO> startEvent(@RequestBody StartEventRequest startEventRequest) {
        Long eventId = startEventRequest.getEventId();
        Event startedEvent;

        if (!eventService.haveAdminAccess(eventId))
            return ResponseEntity.badRequest().build();

        try {
            startedEvent = eventService.startEventById(eventId);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(startedEvent.toEventDTO());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId,
                                             @RequestBody EventRequest requestedEvent) {
        if (!eventService.haveAdminAccess(eventId))
            return ResponseEntity.badRequest().build();

        Event eventToUpdate = requestedEvent.toEvent(eventId);
        EventDTO eventDTO = eventService.createOrUpdateEvent(eventToUpdate).toEventDTO();

        if (eventDTO != null)
            return ResponseEntity.ok(eventDTO);

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hide/{eventId}")
    public ResponseEntity<MessageResponseDTO> hideEvent(@PathVariable Long eventId) {
        if (!eventService.haveAdminAccess(eventId))
            return ResponseEntity.badRequest().build();

        Event.Status result = eventService.hideOrFindOutEvent(eventId);
        return ResponseEntity.ok(
                new MessageResponseDTO("Event with id " + eventId + " have status " + result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<MessageResponseDTO> deleteEventById(@PathVariable Long eventId) {
        if (!eventService.haveAdminAccess(eventId))
            return ResponseEntity.badRequest().build();

        boolean result = eventService.deleteEvent(eventId);
        if (result) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Event with id " + eventId + " deleted successfully"));
        }

        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Event with id " + eventId + " could not delete")
        );
    }
}
