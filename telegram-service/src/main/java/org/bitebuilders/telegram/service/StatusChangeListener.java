package org.bitebuilders.telegram.service;

import org.bitebuilders.telegram.model.ApplicationStatusChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;



@Service
public class StatusChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(StatusChangeListener.class);

    private final RobotExecutorService robotExecutorService;

    public StatusChangeListener(RobotExecutorService robotExecutorService) {
        this.robotExecutorService = robotExecutorService;
    }

    @TransactionalEventListener
    public void onStatusChange(ApplicationStatusChangedEvent event) {
        try {
            logger.info("Status changed for application {} to status {}", event.getApplicationId(), event.getNewStatusId());
            robotExecutorService.executeRobotsForStatus(event.getApplicationId(), event.getNewStatusId());
        } catch (Exception e) {
            logger.info("Failed to handle status change for application {}: {}", event.getApplicationId(), e.getMessage());
        }
    }
}
