package org.bitebuilders.service.schedule;

import org.bitebuilders.model.Event;
import org.bitebuilders.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventUpdateService {

    @Autowired
    private final EventService eventService;

    public EventUpdateService(EventService eventService) {
        this.eventService = eventService;
    }

//    @Scheduled(fixedRate = 600000) // Обновляем статусы каждый час
//    @Transactional
//    public void updateEventConditions() {
//        List<Event> events = eventService.getAllEvents();
//
//        for (Event event : events) {
//            eventService.updateEventStatus(event);
//        }
//    }
}

