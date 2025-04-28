package org.bitebuilders.controller;

import org.bitebuilders.controller.requests.CreateFormRequest;
import org.bitebuilders.controller.requests.UpdateFormRequest;
import org.bitebuilders.model.EventForm;
import org.bitebuilders.model.StandardField;
import org.bitebuilders.service.EventFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class EventFormController {
    private final EventFormService formService;

    @GetMapping("/standard-fields")
    public List<StandardField> getAllStandardFields() {
        return formService.getAllStandardFields();
    }

    public EventFormController(EventFormService formService) {
        this.formService = formService;
    }

    @PostMapping
    public ResponseEntity<EventForm> createForm(
            @RequestBody CreateFormRequest request) {
        // Преобразуем FieldRequest в FormField
        List<Long> fieldIds = request.selectedFieldIds();

        EventForm form = formService.createForm(
                request.eventId(),
                request.title(),
                request.isTemplate(),
                fieldIds
        );
        return ResponseEntity.ok(form);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventForm> updateForm(
            @PathVariable Long id,
            @RequestBody UpdateFormRequest request) {

        return ResponseEntity.ok(formService.updateForm(
                id,
                request.title(),
                request.selectedFieldIds(),
                request.isTemplate()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        formService.deleteForm(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EventForm>> getAllForms(
            @RequestParam(required = false, defaultValue = "false") boolean withFields) {
        return ResponseEntity.ok(formService.getAllForms(withFields));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventForm> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getFormWithFields(id));
    }
}
