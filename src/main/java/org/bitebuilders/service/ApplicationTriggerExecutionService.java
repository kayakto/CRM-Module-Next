package org.bitebuilders.service;

import org.bitebuilders.model.ApplicationTriggerExecution;
import org.bitebuilders.repository.ApplicationTriggerExecutionJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationTriggerExecutionService {

    private final ApplicationTriggerExecutionJdbcRepository executionRepository;

    public ApplicationTriggerExecutionService(ApplicationTriggerExecutionJdbcRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    public void markExecuted(Long applicationId, Long statusId, Long triggerId) {
        executionRepository.markExecuted(applicationId, statusId, triggerId);
    }

    public boolean isExecuted(Long applicationId, Long statusId, Long triggerId) {
        return executionRepository.isExecuted(applicationId, statusId, triggerId);
    }

    public boolean isTriggerExecuted(Long applicationId, Long statusId, Long triggerId) {
        return executionRepository
                .find(applicationId, statusId, triggerId)
                .map(ApplicationTriggerExecution::getExecuted)
                .orElse(false); // по умолчанию — не выполнен
    }
}
