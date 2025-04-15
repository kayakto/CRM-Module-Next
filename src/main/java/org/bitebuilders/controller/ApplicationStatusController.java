package org.bitebuilders.controller;

import org.bitebuilders.model.ApplicationStatus;
import org.bitebuilders.service.ApplicationStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class ApplicationStatusController {

    private final ApplicationStatusService statusService;

    public ApplicationStatusController(ApplicationStatusService statusService) {
        this.statusService = statusService;
    }

    // Создание статуса
    @PostMapping
    public ResponseEntity<ApplicationStatus> createStatus(@RequestBody ApplicationStatus status) {
        ApplicationStatus createdStatus = statusService.createStatus(status);
        return ResponseEntity.ok(createdStatus);
    }

    // Обновление статуса
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationStatus> updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatus newStatus) {

        return statusService.updateStatus(id, newStatus)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Удаление статуса
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        if (statusService.deleteStatus(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Получение всех статусов
    @GetMapping
    public ResponseEntity<List<ApplicationStatus>> getAllStatuses() {
        List<ApplicationStatus> statuses = statusService.getAllStatuses();
        return ResponseEntity.ok(statuses);
    }
}
