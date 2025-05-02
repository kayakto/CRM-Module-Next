package org.bitebuilders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.requests.ApplicationStatusRequest;
import org.bitebuilders.controller.requests.StatusRequest;
import org.bitebuilders.controller.requests.StatusUpdateRequest;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.ApplicationStatus;
import org.bitebuilders.service.ApplicationStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
public class ApplicationStatusController {
    private final ApplicationStatusService statusService;

    // Создание глобального статуса
    @PostMapping
    public ResponseEntity<ApplicationStatus> createStatus(
            @Valid @RequestBody StatusRequest request) {
        ApplicationStatus status = new ApplicationStatus();
        status.setName(request.getName());
        status.setDisplayOrder(request.getDisplayOrder());

        ApplicationStatus createdStatus = statusService.createGlobalStatus(status);
        return ResponseEntity.ok(createdStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationStatus> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        ApplicationStatus statusUpdate = new ApplicationStatus();
        statusUpdate.setName(request.getName());
        statusUpdate.setDisplayOrder(request.getDisplayOrder());

        return statusService.updateGlobalStatus(id, statusUpdate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Удаление глобального статуса
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        try {
            statusService.deleteGlobalStatus(id);
            return ResponseEntity.noContent().build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Получение всех глобальных статусов
    @GetMapping
    public ResponseEntity<List<ApplicationStatus>> getAllStatuses() {
        List<ApplicationStatus> statuses = statusService.getAllGlobalStatuses();
        return ResponseEntity.ok(statuses);
    }
}
