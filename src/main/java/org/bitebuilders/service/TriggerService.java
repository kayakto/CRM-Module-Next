package org.bitebuilders.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.Trigger;
import org.bitebuilders.repository.TriggerJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TriggerService {
    private final TriggerJdbcRepository triggerRepository;

    public Trigger createTrigger(Trigger trigger) {
        return triggerRepository.save(trigger);
    }

    public Optional<Trigger> getTriggerById(Long id) {
        return triggerRepository.findById(id);
    }

    public void deleteTrigger(Long id) {
        triggerRepository.delete(id);
    }
}
