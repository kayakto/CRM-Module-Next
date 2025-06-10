package org.bitebuilders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.DisplayOrderUpdateRequest;
import org.bitebuilders.controller.requests.StatusRequest;
import org.bitebuilders.controller.requests.StatusUpdateRequest;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.ApplicationStatus;
import org.bitebuilders.service.ApplicationStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events/{eventId}/statuses")
@RequiredArgsConstructor
public class EventStatusController {
    private final ApplicationStatusService statusService;

    @GetMapping
    public ResponseEntity<?> getEventStatuses(@PathVariable Long eventId) {
        try {
            List<ApplicationStatus> statuses = statusService.getStatusesByEvent(eventId);
            return ResponseEntity.ok(statuses);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addStatus(
            @PathVariable Long eventId,
            @Valid @RequestBody StatusRequest request) {
        try {
            ApplicationStatus status = new ApplicationStatus();
            status.setName(request.getName());
            status.setDisplayOrder(request.getDisplayOrder());

            ApplicationStatus createdStatus = statusService.addStatusToEvent(eventId, status);
            return ResponseEntity.ok(createdStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long eventId,
            @PathVariable Long statusId,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            ApplicationStatus statusUpdate = new ApplicationStatus();
            statusUpdate.setName(request.getName());
            statusUpdate.setDisplayOrder(request.getDisplayOrder());

            ApplicationStatus updatedStatus = statusService.updateEventStatus(
                    eventId,
                    statusId,
                    statusUpdate
            );
            return ResponseEntity.ok(updatedStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderStatuses(
            @PathVariable Long eventId,
            @RequestBody DisplayOrderUpdateRequest orders
    ) {
        try {
            statusService.updateDisplayOrders(eventId, orders.getParsedOrders());
            return ResponseEntity.ok("Display order обновлён для статусов");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<?> removeStatus(
            @PathVariable Long eventId,
            @PathVariable Long statusId) {
        try {
            statusService.removeStatusFromEvent(eventId, statusId);
            return ResponseEntity.noContent().build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
