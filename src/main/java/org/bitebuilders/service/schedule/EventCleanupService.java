package org.bitebuilders.service.schedule;

import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventCleanupService {

    private final EventRepository eventRepository;

    public EventCleanupService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

//    @Scheduled(cron = "0 0 0 * * ?") // Запуск каждый день в полночь
    @Transactional
    public void cleanUpDeletedEvents() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        eventRepository.deleteByConditionAndUpdatedBefore(Event.Status.DELETED, thirtyDaysAgo);
    }
}
