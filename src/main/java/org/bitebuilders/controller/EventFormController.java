package org.bitebuilders.controller;

import org.bitebuilders.controller.requests.CreateFormRequest;
import org.bitebuilders.controller.requests.FieldRequest;
import org.bitebuilders.controller.requests.UpdateFormRequest;
import org.bitebuilders.model.EventForm;
import org.bitebuilders.model.FormField;
import org.bitebuilders.service.EventFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class EventFormController {
    private final EventFormService formService;

    public EventFormController(EventFormService formService) {
        this.formService = formService;
    }

    @PostMapping
    public ResponseEntity<EventForm> createForm(
            @RequestBody CreateFormRequest request) {
        // Преобразуем FieldRequest в FormField
        List<FormField> fields = request.fields().stream()
                .map(FieldRequest::toFormField)
                .toList();

        EventForm form = formService.createForm(
                request.eventId(),
                request.title(),
                fields
        );
        return ResponseEntity.ok(form);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventForm> updateForm(
            @PathVariable Long id,
            @RequestBody UpdateFormRequest request) {  // Используем DTO для тела запроса

        List<FormField> fields = request.fields().stream()
                .map(FieldRequest::toFormField)
                .toList();

        return ResponseEntity.ok(formService.updateForm(
                id,
                request.title(),
                fields
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        formService.deleteForm(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EventForm>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventForm> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getFormWithFields(id));
    }
}
