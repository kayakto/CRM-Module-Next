package org.bitebuilders.controller;

import org.bitebuilders.controller.requests.CreateTriggerRequest;
import org.bitebuilders.controller.requests.LinkTriggerToStatusRequest;
import org.bitebuilders.model.Trigger;
import org.bitebuilders.service.StatusTriggerService;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/status-triggers")
@RequiredArgsConstructor
public class StatusTriggerController {

    private final StatusTriggerService service;

    @GetMapping("/{statusId}")
    public ResponseEntity<List<Trigger>> getTriggers(@PathVariable Long statusId) {
        return ResponseEntity.ok(service.getTriggersByStatusId(statusId));
    }

    @PostMapping("/{statusId}")
    public ResponseEntity<Void> addTriggerToStatus(
            @PathVariable Long statusId,
            @RequestBody @Valid LinkTriggerToStatusRequest request
    ) {
        service.linkTriggerToStatus(statusId, request.getTriggerId(), request.getParameters());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{statusId}/{triggerId}")
    public ResponseEntity<Void> deleteTriggerFromStatus(
            @PathVariable Long statusId,
            @PathVariable Long triggerId
    ) {
        service.unlinkTriggerFromStatus(statusId, triggerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{statusId}/{triggerId}/executed")
    public ResponseEntity<Void> setExecuted(
            @PathVariable Long statusId,
            @PathVariable Long triggerId
    ) {
        service.markTriggerAsExecuted(statusId, triggerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{statusId}/{triggerId}/parameters")
    public ResponseEntity<Void> updateParameters(
            @PathVariable Long statusId,
            @PathVariable Long triggerId,
            @RequestBody Map<String, Object> parameters
    ) {
        service.updateStatusTriggerParameters(statusId, triggerId, parameters);
        return ResponseEntity.noContent().build();
    }
}


