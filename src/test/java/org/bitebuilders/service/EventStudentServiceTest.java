package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.*;
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
public class EventStudentServiceTest {

    @Autowired
    private EventStudentService eventStudentService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStudentRepository eventStudentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    // Глобальные переменные для хранения ID
    private Long studentId;
    private Long eventId;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        eventStudentRepository.deleteAll();
        userInfoRepository.deleteAll();
        eventRepository.deleteAll();

        // Добавление тестовых данных
        UserInfo student = new UserInfo(
                "John",
                "Doe",
                null,
                "johndoe@example.com",
                "John's sign",
                "vk.com/johndoe",
                "t.me/johndoe",
                UserRole.STUDENT,
                "Chill student"

        );
        studentId = userInfoRepository.save(student).getId();

        Event event = new Event(
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
        eventId = eventRepository.save(event).getId();

        EventStudent eventCurator = new EventStudent(
                studentId,
                eventId,
                StatusRequest.SENT_PERSONAL_INFO
        );
        eventStudentRepository.save(eventCurator);
    }

    @Test
    void testGetEventStudent() {
        EventStudent result = eventStudentService.getEventStudent(eventId, studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals(eventId, result.getEventId());
        assertEquals(StatusRequest.SENT_PERSONAL_INFO, result.getStudentStatus());
    }

    @Test
    void testGetStudentsInfo() {
        List<EventStudentInfo> result = eventStudentService.getEventStudents(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());
    }

    @Test
    void testGetSentStudentInfo() {
        List<EventStudentInfo> result = eventStudentService.getSentStudentInfo(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(StatusRequest.SENT_PERSONAL_INFO, result.get(0).getStatusRequest());
    }

    @Test
    void testUpdateStudentStatus() {
        Boolean result = eventStudentService.updateStudentStatus(eventId, studentId, StatusRequest.ADDED_IN_CHAT);

        assertTrue(result);

        EventStudent updatedStudent = eventStudentService.getEventStudent(eventId, studentId);
        assertEquals(StatusRequest.ADDED_IN_CHAT, updatedStudent.getStudentStatus());
    }

    @Test
    void testUpdateCuratorStatusThrowsExceptionWhenCuratorNotFound() {
        Long newEventId = 228228228228228L; // Несуществующий eventId

        assertThrows(EventUserNotFoundException.class, () ->
                eventStudentService.updateStudentStatus(newEventId, studentId, StatusRequest.ADDED_IN_CHAT)
        );
    }
}
