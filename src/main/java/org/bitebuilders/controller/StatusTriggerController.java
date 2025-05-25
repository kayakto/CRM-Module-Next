package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.StatusTriggerDto;
import org.bitebuilders.controller.requests.CreateTriggerRequest;
import org.bitebuilders.controller.requests.LinkTriggerToStatusRequest;
import org.bitebuilders.model.Trigger;
import org.bitebuilders.service.ApplicationTriggerExecutionService;
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

    private final StatusTriggerService statusTriggerService;

    private final ApplicationTriggerExecutionService applicationTriggerExecutionService;

    @GetMapping("/{statusId}")
    public ResponseEntity<List<StatusTriggerDto>> getTriggers(@PathVariable Long statusId) {
        return ResponseEntity.ok(statusTriggerService.getTriggersByStatusId(statusId));
    }

    @PostMapping("/{statusId}")
    public ResponseEntity<StatusTriggerDto> addTriggerToStatus(
            @PathVariable Long statusId,
            @RequestBody @Valid LinkTriggerToStatusRequest request
    ) {
        StatusTriggerDto dto = statusTriggerService.linkTriggerToStatus(statusId, request.getTriggerId(), request.getParameters());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{applicationId}/{statusId}/{triggerId}/executed")
    public ResponseEntity<Boolean> isTriggerExecuted(
            @PathVariable Long applicationId,
            @PathVariable Long statusId,
            @PathVariable Long triggerId
    ) {
        boolean executed = applicationTriggerExecutionService.isTriggerExecuted(applicationId, statusId, triggerId);
        return ResponseEntity.ok(executed);
    }

    @DeleteMapping("/{statusId}/{triggerId}")
    public ResponseEntity<Void> deleteTriggerFromStatus(
            @PathVariable Long statusId,
            @PathVariable Long triggerId
    ) {
        statusTriggerService.unlinkTriggerFromStatus(statusId, triggerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{applicationId}/{statusId}/{triggerId}/executed")
    public ResponseEntity<Void> setExecuted(
            @PathVariable Long applicationId,
            @PathVariable Long statusId,
            @PathVariable Long triggerId
    ) {
        applicationTriggerExecutionService.markExecuted(applicationId, statusId, triggerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{statusId}/{triggerId}/parameters")
    public ResponseEntity<StatusTriggerDto> updateParameters(
            @PathVariable Long statusId,
            @PathVariable Long triggerId,
            @RequestBody Map<String, Object> parameters
    ) {
        StatusTriggerDto dto = statusTriggerService.updateStatusTriggerParameters(statusId, triggerId, parameters);
        return ResponseEntity.ok(dto);
    }
}


