package org.bitebuilders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.requests.CreateTriggerRequest;
import org.bitebuilders.model.Trigger;
import org.bitebuilders.service.TriggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/triggers")
@RequiredArgsConstructor
public class TriggerController {

    private final TriggerService triggerService;

    @PostMapping
    public ResponseEntity<Trigger> createTrigger(@RequestBody @Valid CreateTriggerRequest request) {
        Trigger trigger = new Trigger();
        trigger.setName(request.getName());
        trigger.setType(request.getType());
        trigger.setParameters(request.getParameters());
        trigger.setCreatedAt(OffsetDateTime.now());

        Trigger saved = triggerService.createTrigger(trigger);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trigger> getTriggerById(@PathVariable Long id) {
        return triggerService.getTriggerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrigger(@PathVariable Long id) {
        triggerService.deleteTrigger(id);
        return ResponseEntity.noContent().build();
    }
}
