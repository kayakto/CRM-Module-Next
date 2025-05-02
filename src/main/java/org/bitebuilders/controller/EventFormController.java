package org.bitebuilders.controller;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.FormDTO;
import org.bitebuilders.controller.requests.CreateOrUpdateFormRequest;
import org.bitebuilders.model.StandardField;
import org.bitebuilders.model.SystemField;
import org.bitebuilders.service.EventFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forms")
@RequiredArgsConstructor
public class EventFormController {
    private final EventFormService formService;

    @GetMapping("/system-fields")
    public List<SystemField> getAllSystemFields() {
        return formService.getAllSystemFields();
    }

    @GetMapping("/standard-fields")
    public List<StandardField> getAllStandardFields() {
        return formService.getAllStandardFields();
    }

    @PostMapping
    public ResponseEntity<FormDTO> createOrUpdateForm(
            @RequestBody CreateOrUpdateFormRequest request) {
        return ResponseEntity.ok(formService.createOrUpdateForm(request));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long eventId) {
        formService.deleteForm(eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FormDTO>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<FormDTO> getFormByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(formService.getFormByEventId(eventId));
    }
}
