package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.EventUserNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class EventCuratorServiceTest {

    @Autowired
    private EventCuratorService eventCuratorService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCuratorRepository eventCuratorRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    // Глобальные переменные для хранения ID
    private Long curatorId;
    private Long eventId;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        eventCuratorRepository.deleteAll();
        userInfoRepository.deleteAll();
        eventRepository.deleteAll();

        // Добавление тестовых данных
        UserInfo curator = new UserInfo(
                "John",
                "Doe",
                null,
                "johndoe@example.com",
                "John's sign",
                "vk.com/johndoe",
                "t.me/johndoe",
                UserRole.CURATOR,
                "Chill guy"

        );
        curatorId = userInfoRepository.save(curator).getId();

        Event event1 = new Event(
                "description",
                "title",
                null,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );
        eventId = eventRepository.save(event1).getId();

        EventCurator eventCurator = new EventCurator(
                curatorId,
                eventId,
                StatusRequest.SENT_PERSONAL_INFO
        );
        eventCuratorRepository.save(eventCurator);
    }

    @Test
    void testGetEventCurator() {
        EventCurator result = eventCuratorService.getEventCurator(eventId, curatorId);

        assertNotNull(result);
        assertEquals(curatorId, result.getCuratorId());
        assertEquals(eventId, result.getEventId());
        assertEquals(StatusRequest.SENT_PERSONAL_INFO, result.getCuratorStatus());
    }

    @Test
    void testGetCuratorsInfo() {
        List<EventCuratorInfo> result = eventCuratorService.getCuratorsInfo(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());
    }

    @Test
    void testUpdateCuratorStatus() {
        Boolean result = eventCuratorService.updateCuratorStatus(eventId, curatorId, StatusRequest.ADDED_IN_CHAT);

        assertTrue(result);

        EventCurator updatedCurator = eventCuratorService.getEventCurator(eventId, curatorId);
        assertEquals(StatusRequest.ADDED_IN_CHAT, updatedCurator.getCuratorStatus());
    }

    @Test
    void testUpdateCuratorStatusThrowsExceptionWhenCuratorNotFound() {
        Long newEventId = 111111111111111L; // Несуществующий eventId

        assertThrows(EventUserNotFoundException.class, () ->
                eventCuratorService.updateCuratorStatus(newEventId, curatorId, StatusRequest.ADDED_IN_CHAT)
        );
    }

    @Test
    void testGetSentCuratorInfo() {
        List<EventCuratorInfo> result = eventCuratorService.getSentCuratorInfo(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(StatusRequest.SENT_PERSONAL_INFO, result.get(0).getCuratorStatus());
    }
}

