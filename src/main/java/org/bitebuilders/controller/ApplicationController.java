package org.bitebuilders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.ApplicationDTO;
import org.bitebuilders.controller.requests.CreateApplicationRequest;
import org.bitebuilders.controller.requests.UpdateApplicationStatusRequest;
import org.bitebuilders.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationDTO> createApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createApplication(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationDTO>> getApplications(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long statusId) {
        return ResponseEntity.ok(applicationService.getApplications(eventId, statusId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplication(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByEvent(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(applicationService.getApplicationsByEvent(eventId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplicationDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateApplicationStatusRequest updateRequest) {
        return ResponseEntity.ok(applicationService.updateStatus(id, updateRequest.statusId()));
    }
}
