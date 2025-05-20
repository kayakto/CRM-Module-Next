package org.bitebuilders.telegram.service;


import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.repository.ApplicationRepository;

import java.util.Optional;

public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }


    public Optional<Application> findByTelegramUrl(String telegramUrl) {
        return applicationRepository.findByTelegramUrl(telegramUrl);
    }
}
