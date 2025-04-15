//package org.bitebuilders.controller;
//
//import org.bitebuilders.controller.dto.MessageResponseDTO;
//import org.bitebuilders.controller.requests.StudentTestResultRequest;
//import org.bitebuilders.enums.StatusRequest;
//import org.bitebuilders.service.EventService;
//import org.bitebuilders.service.EventStudentService;
//import org.bitebuilders.service.TestResultService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/events-students")
//public class EventStudentController {
//
//    private final EventStudentService eventStudentService;
//    private final EventService eventService;
//    private final TestResultService testResultService;
//
//    @Autowired
//    public EventStudentController(EventStudentService eventStudentService, EventService eventService, TestResultService testResultService) {
//        this.eventStudentService = eventStudentService;
//        this.eventService = eventService;
//        this.testResultService = testResultService;
//    }
//
//    // Получение всех студентов, отправивших персональные данные для участия
////    @PreAuthorize("hasRole('ADMIN')")
////    @GetMapping("/{eventId}/students")
////    public ResponseEntity<List<EventStudentInfoDTO>> getStudentsInfo(@PathVariable Long eventId) {
////        if (!eventService.haveAdminAccess(eventId)) {
////            return ResponseEntity.badRequest().build();
////        }
////
////        List<EventStudentInfoDTO> esInfoDTO = eventStudentService.getEventStudents(eventId)
////                .stream()
////                .map(EventStudentInfo::toEventStudentDTO)
////                .toList();
////
////        if (esInfoDTO != null)
////            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов и их статусы
////
////        return ResponseEntity.noContent().build();
////    }
////
//    @GetMapping("/{eventId}/student-can-send/{studentId}")
//    public ResponseEntity<MessageResponseDTO> canSendStudent(
//            @PathVariable Long eventId,
//            @PathVariable Long studentId ) {
//        boolean hasAbility = eventStudentService.canSend(eventId, studentId);
//
//        return ResponseEntity.ok(
//                new MessageResponseDTO(String.valueOf(hasAbility))
//        );
//    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    @GetMapping("/{eventId}/waiting-students")
////    public ResponseEntity<List<EventStudentInfoDTO>> getWaitingStudentsInfo(@PathVariable Long eventId) {
////        if (!eventService.haveManagerAccess(eventId)) {
////            return ResponseEntity.badRequest().build();
////        }
////
////        List<EventStudentInfoDTO> esInfoDTO = eventStudentService
////                .getWaitingStudentInfo(eventId)
////                .stream()
////                .map(EventStudentInfo::toEventStudentDTO)
////                .toList();
////
////        if (esInfoDTO != null)
////            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов
////
////        return ResponseEntity.noContent().build();
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    @GetMapping("/{eventId}/accepted-students")
////    public ResponseEntity<List<EventStudentInfoDTO>> getAcceptedStudentsInfo(@PathVariable Long eventId) {
////        if (!eventService.haveManagerAdminAccess(eventId)) {
////            return ResponseEntity.badRequest().build();
////        }
////
////        List<EventStudentInfoDTO> esInfoDTO = eventStudentService
////                .getAcceptedStudentInfo(eventId)
////                .stream()
////                .map(EventStudentInfo::toEventStudentDTO)
////                .toList();
////
////        if (esInfoDTO != null)
////            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов
////
////        return ResponseEntity.noContent().build();
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    @GetMapping("/{eventId}/started-students")
////    public ResponseEntity<List<EventStudentInfoDTO>> getStartedStudentsInfo(@PathVariable Long eventId) {
////        if (!eventService.haveManagerAdminCuratorAccess(eventId)) {
////            return ResponseEntity.badRequest().build();
////        }
////
////        List<EventStudentInfoDTO> esInfoDTO = eventStudentService
////                .getStartedStudentInfo(eventId)
////                .stream()
////                .map(EventStudentInfo::toEventStudentDTO)
////                .toList();
////
////        if (esInfoDTO != null)
////            return ResponseEntity.ok(esInfoDTO);
////
////        return ResponseEntity.noContent().build();
////    }
//
//    /**
//     * Метод отправки заявки студента на мероприятие
//     */
//    @PutMapping("/{eventId}/send/{studentId}")
//    public ResponseEntity<MessageResponseDTO> sendStudentToEvent(
//            @PathVariable Long eventId,
//            @PathVariable Long studentId
//    ) {
//        boolean isUpdated = eventStudentService.updateStudentStatus(
//                eventId,
//                studentId,
//                StatusRequest.SENT_PERSONAL_INFO
//        );
//
//        if (isUpdated) {
//            return ResponseEntity.ok(
//                    new MessageResponseDTO("Student with id " + studentId + " sent request to event " + eventId));
//        }
//        return ResponseEntity.badRequest().body(
//                new MessageResponseDTO("Student with id " + studentId + " cannot send request to event " + eventId)
//        ); // todo проверить, не закончились ли места
//    }
//
//    /**
//     * Метод принятия заявки студента на мероприятие. (становится участником мероприятия)
//     */
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{eventId}/accept/{studentId}")
//    public ResponseEntity<MessageResponseDTO> acceptStudentRequest(
//            @PathVariable Long eventId,
//            @PathVariable Long studentId
//    ) {
//        if (!eventService.haveAdminAccess(eventId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (!eventStudentService.isAllowedStatus(eventId, studentId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        boolean isUpdated = eventStudentService.updateStudentStatus(
//                eventId,
//                studentId,
//                StatusRequest.ADDED_IN_CHAT
//        );
//
//        if (isUpdated) {
//            return ResponseEntity.ok(
//                    new MessageResponseDTO("Student with id " + studentId + " accepted to event " + eventId));
//        }
//        return ResponseEntity.badRequest().body(
//                new MessageResponseDTO("Student with id " + studentId + " cannot be accepted to event " + eventId)
//        );
//    } // TODO add student to chat
//
//    /**
//     * Метод отклонения заявки студента на мероприятие (reject)
//     */
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{eventId}/reject/{studentId}")
//    public ResponseEntity<MessageResponseDTO> rejectStudentRequest(
//            @PathVariable Long eventId,
//            @PathVariable Long studentId
//    ) {
//        if (!eventService.haveAdminAccess(eventId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (!eventStudentService.isAllowedStatus(eventId, studentId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        boolean isUpdated = eventStudentService.updateStudentStatus(
//                eventId,
//                studentId,
//                StatusRequest.REJECTED_FROM_EVENT
//        );
//
//        if (isUpdated) {
//            return ResponseEntity.ok(
//                    new MessageResponseDTO("Student with id " + studentId + " rejected from event " + eventId));
//        }
//        return ResponseEntity.badRequest().body(
//                new MessageResponseDTO("Student with id " + studentId + " cannot be rejected from event " + eventId)
//        );
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/test-result")
//    public ResponseEntity<MessageResponseDTO> saveTestResult(@RequestBody StudentTestResultRequest resultRequest) {
//        if (testResultService.
//                addOrUpdateResult(
//                        resultRequest.toStudentTestresult()).getId() == null)
//            return ResponseEntity.badRequest().body(new MessageResponseDTO("Could not save result for this test"));
//
//        Long eventId = resultRequest.getEventId();
//        Long studentId = resultRequest.getStudentId();
//
//        boolean isUpdated = eventStudentService
//                .updateStudentStatus(
//                        eventId,
//                        studentId,
//                        resultRequest.isPassed() ? StatusRequest.TEST_PASSED : StatusRequest.TEST_FAILED);
//
//        if (isUpdated) {
//            return ResponseEntity.ok(
//                    new MessageResponseDTO("Result successfully saved for student " + studentId + " in event " + eventId)
//            );
//        }
//
//        return ResponseEntity.badRequest().body(
//                new MessageResponseDTO("WARNING! Could not save result for student " + studentId + " in event " + eventId)
//        );
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{eventId}/delete/{studentId}")
//    public ResponseEntity<MessageResponseDTO> deleteStudentFromEvent(
//            @PathVariable Long eventId,
//            @PathVariable Long studentId) {
//        if (!eventService.haveAdminAccess(eventId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        boolean isDeleted = eventStudentService
//                .updateStudentStatus(
//                        eventId,
//                        studentId,
//                        StatusRequest.DELETED_FROM_EVENT);
//
//        if (isDeleted) {
//            return ResponseEntity.ok(
//                    new MessageResponseDTO("Student " + studentId + " successfully deleted from event " + eventId)
//            );
//        }
//
//        return ResponseEntity.badRequest().body(
//                new MessageResponseDTO("Student " + studentId + " cannot be deleted from event " + eventId)
//        );
//    }
//}
