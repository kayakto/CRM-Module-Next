package org.bitebuilders.service;

import org.bitebuilders.model.ApplicationStatus;
import org.bitebuilders.repository.ApplicationStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationStatusService {

    private final ApplicationStatusRepository statusRepository;

    public ApplicationStatusService(ApplicationStatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    // Создание нового статуса
    @Transactional
    public ApplicationStatus createStatus(ApplicationStatus status) {
        return statusRepository.save(status);
    }

    // Обновление статуса по ID
    @Transactional
    public Optional<ApplicationStatus> updateStatus(Long id, ApplicationStatus newStatus) {
        return statusRepository.findById(id)
                .map(existingStatus -> {
                    existingStatus.setName(newStatus.getName());
                    existingStatus.setUpdatedAt(OffsetDateTime.now());
                    return statusRepository.save(existingStatus);
                });
    }

    // Удаление статуса по ID
    @Transactional
    public boolean deleteStatus(Long id) {
        if (statusRepository.existsById(id)) {
            statusRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Получение всех статусов
    public List<ApplicationStatus> getAllStatuses() {
        return (List<ApplicationStatus>) statusRepository.findAll();
    }

    // Получение статуса по ID
    public Optional<ApplicationStatus> getStatusById(Long id) {
        return statusRepository.findById(id);
    }
}
