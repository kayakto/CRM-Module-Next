//package org.bitebuilders.controller;
//
//import org.bitebuilders.enums.StatusRequest;
//import org.bitebuilders.model.Event;
//import org.bitebuilders.model.UserInfo;
//import org.bitebuilders.repository.EventRepository;
//import org.bitebuilders.repository.UserInfoRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class EventStudentControllerTest {
//
//    @Autowired
//    private EventStudentController eventStudentController;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private UserInfoRepository userInfoRepository;
//
//    // Глобальные переменные для хранения ID
//    private Long studentId;
//    private Long eventId;
//    private Long studentIdWithoutEvent;
//
//    @BeforeEach
//    void setUp() {
//        // Очистка базы перед каждым тестом
//        eventStudentRepository.deleteAll();
//        eventRepository.deleteAll();
//        userInfoRepository.deleteAll();
//
//        // Добавление тестовых данных
//        UserInfo student = new UserInfo(
//                "John",
//                "Doe",
//                null,
//                "johndoe@example.com",
//                "John's sign",
//                "vk.com/johndoe",
//                "t.me/johndoe",
//                UserInfo.Role.STUDENT,
//                "Chill guy"
//
//        );
//        studentId = userInfoRepository.save(student).getId();
//
//        UserInfo studentWithoutEvent = new UserInfo(
//                "Andrew",
//                "Doe",
//                null,
//                "doe@example.com",
//                "Andrew's sign",
//                "vk.com/andrewdoe",
//                "t.me/andrewdoe",
//                Role.STUDENT,
//                "Chill guy"
//
//        );
//        studentIdWithoutEvent = userInfoRepository.save(studentWithoutEvent).getId();
//
//        Event event = new Event(
//                Event.Status.REGISTRATION_OPEN,
//                "description",
//                "title",
//                null,
//                null,
//                OffsetDateTime.now(),
//                OffsetDateTime.now(),
//                OffsetDateTime.now(),
//                OffsetDateTime.now(),
//                null,
//                100
//        );
//        eventId = eventRepository.save(event).getId();
//
//        EventStudent eventStudent = new EventStudent(
//                studentId,
//                eventId,
//                StatusRequest.SENT_PERSONAL_INFO
//        );
//        eventStudentRepository.save(eventStudent);
//    }
//
//    @Test
//    void testGetStudentsInfo() {
//        ResponseEntity<List<EventStudentInfoDTO>> response = eventStudentController.getStudentsInfo(eventId);
//
//        assertNotNull(response);
//        assertTrue(response.getBody() != null && !response.getBody().isEmpty());
//        assertEquals(1, response.getBody().size());
//        assertEquals(
//                StatusRequest.SENT_PERSONAL_INFO,
//                response.getBody().get(0).getStatusRequest());
//    }
//
//    @Test
//    void testSendStudentRequest() {
//        var response = eventStudentController.sendStudentToEvent(eventId, studentIdWithoutEvent);
//
//        assertNotNull(response);
//        assertTrue(response.getBody());
//        assertEquals(
//                StatusRequest.SENT_PERSONAL_INFO,
//                eventStudentRepository.findStudentEvent(studentIdWithoutEvent, eventId).get().getStudentStatus());
//    }
//
//    @Test
//    void testGetSentStudentsInfo() {
//        // добавим второго куратора на мероприятие
//        eventStudentController.sendStudentToEvent(eventId, studentIdWithoutEvent);
//        var response = eventStudentController.getSentStudentsInfo(eventId);
//
//        assertNotNull(response);
//        assertTrue(response.getBody() != null && !response.getBody().isEmpty());
//        assertEquals(2, response.getBody().size());
//        assertEquals(
//                StatusRequest.SENT_PERSONAL_INFO,
//                response.getBody().get(0).getStatusRequest());
//    }
//
//    @Test
//    void testDeleteStudentFromEvent() {
//        var response = eventStudentController.deleteStudentFromEvent(eventId, studentId);
//
//        assertNotNull(response);
//        assertTrue(response.getBody());
//        assertEquals(
//                StatusRequest.DELETED_FROM_EVENT,
//                eventStudentRepository.findStudentEvent(studentId, eventId).get().getStudentStatus());
//    }
//
//    @Test
//    void testAcceptStudentRequest() {
//        var response = eventStudentController.acceptStudentRequest(eventId, studentId);
//
//        assertNotNull(response);
//        assertTrue(response.getBody());
//        assertEquals(
//                StatusRequest.ADDED_IN_CHAT,
//                eventStudentRepository.findStudentEvent(studentId, eventId).get().getStudentStatus());
//    }
//
//    @Test
//    void testRejectStudentRequest() {
//        var response = eventStudentController.rejectStudentRequest(eventId, studentId);
//
//        assertNotNull(response);
//        assertTrue(response.getBody());
//        assertEquals(
//                StatusRequest.REJECTED_FROM_EVENT,
//                eventStudentRepository.findStudentEvent(studentId, eventId).get().getStudentStatus());
//    }
//    // TODO тесты для eventController, для userInfo и для всех репо
//}
