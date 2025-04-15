package org.bitebuilders.service; // для теста старта отдельного event в EventServiceStartTest

import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EventService eventService;

    private Long adminId;

    private Long eventId;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        eventRepository.deleteAll();
        userInfoRepository.deleteAll();

        // Создаем админа
        UserInfo admin = new UserInfo(
                "John",
                "Doe",
                null,
                "johndoe@example.com",
                "John's sign",
                "vk.com/johndoe",
                "t.me/johndoe",
                UserRole.ADMIN,
                "Chill guy"
        );

        // Сохраняем админа в базе данных
        adminId = userInfoRepository.save(admin).getId();

        // Создаем запись события
        Event event1 = new Event(
                "description",
                "title",
                adminId, // Используем сохраненный adminId
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );

        // Сохраняем событие в базе данных
        eventId = eventRepository.save(event1).getId();
    }

    @Test
    void getOpenedEvents_ShouldReturnOpenedOpenedOpenedEvents() {
        // Act
        List<Event> openedEvents = eventService.getOpenedEvents();

        // Assert
        assertNotNull(openedEvents);
        assertEquals(1, openedEvents.size());
        assertEquals(Event.Status.REGISTRATION_OPEN, openedEvents.get(0).getStatus());
    }

    @Test
    void getEventById_ShouldReturnEventWhenExists() {
        // Act
        Event result = eventService.getEventById(eventId);

        // Assert
        assertNotNull(result);
        assertEquals(eventId, result.getId());
    }

    @Test
    void createOrUpdateEvent_ShouldSaveAndReturnEvent() {
        // Arrange
        Event event = new Event(
                "description",
                "title",
                adminId,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );

        // Act
        Event savedEvent = eventService.createOrUpdateEvent(event);

        // Assert
        assertNotNull(savedEvent);
        assertEquals("title", savedEvent.getTitle());
    }

    @Test
    void deleteEvent_ShouldSetStatusToDeletedWhenEventExists() {
        // Act
        Boolean result = eventService.deleteEvent(eventId);

        // Assert
        assertTrue(result);
        Event updatedEvent = eventRepository.findById(eventId).orElseThrow();
        assertEquals(Event.Status.DELETED, updatedEvent.getStatus());
    }

    @Test
    void deleteEvent_ShouldReturnFalseWhenEventDoesNotExist() {
        // Act
        Boolean result = eventService.deleteEvent(999L);

        // Assert
        assertFalse(result);
    }
}

