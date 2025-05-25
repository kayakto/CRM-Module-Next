package org.bitebuilders.telegram.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.model.Robot;
import org.bitebuilders.telegram.model.StatusTrigger;
import org.bitebuilders.telegram.repository.ApplicationRepository;
import org.bitebuilders.telegram.repository.RobotRepository;
import org.bitebuilders.telegram.repository.StatusTriggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TriggerExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerExecutionService.class);

    private final StatusTriggerRepository statusTriggerRepository;
    private final ApplicationRepository applicationRepository;
    private final RobotExecutorService robotExecutorService;
    private final RobotRepository robotRepository;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 10000)
    public void checkTriggersAndExecuteRobots() {
        logger.info("Starting checkTriggersAndExecuteRobots at {}", Instant.now()); // Changed to info for visibility
        try {
            List<Long> statusIds = applicationRepository.findAllStatusIdsWithApplications();
            logger.debug("Found {} status IDs with applications: {}", statusIds.size(), statusIds);
            for (Long statusId : statusIds) {
                List<Application> applications = applicationRepository.findByStatusId(statusId);
                logger.debug("Found {} applications for status {}: {}",
                        applications.size(), statusId,
                        applications.stream().map(Application::getId).toList());
                for (Application application : applications) {
                    logger.debug("Processing application {} on status {}",
                            application.getId(), application.getStatusId());
                    List<Robot> robots = robotRepository.fetchRobotsForStatus(statusId);
                    if (!robots.isEmpty()) {
                        executeRobotsForApplication(application, statusId);
                    } else {
                        logger.debug("No robots found for status {}", statusId);
                    }
                    checkTriggersForApplication(application);
                }
            }
        } catch (Exception e) {
            logger.error("Error in checkTriggersAndExecuteRobots: {}", e.getMessage(), e);
        }
    }

    private void executeRobotsForApplication(Application application, Long statusId) {
        try {
            logger.debug("Executing robots for application {} and status {}",
                    application.getId(), statusId);
            robotExecutorService.executeRobotsForStatus(application.getId(), statusId);
        } catch (TelegramApiException e) {
            logger.error("Failed to execute robots for application {} and status {}: {}",
                    application.getId(), statusId, e.getMessage(), e);
        }
    }

    private void checkTriggersForApplication(Application application) {
        try {
            // Check triggers for the current status
            String sql = "SELECT COUNT(*) FROM status_triggers WHERE status_id = ?";
            Integer triggerCount = jdbcTemplate.queryForObject(sql,
                    new Object[]{application.getStatusId()}, Integer.class);
            logger.debug("Found {} triggers for status {}", triggerCount, application.getStatusId());
            if (triggerCount != null && triggerCount > 0) {
                List<StatusTrigger> triggers = statusTriggerRepository.findAllWithTriggerDetailsByStatus(
                        application.getStatusId());
                logger.debug("Found {} triggers for application {} on status {}",
                        triggers.size(), application.getId(), application.getStatusId());
                for (StatusTrigger trigger : triggers) {
                    if (shouldTrigger(application, trigger)) {
                        logger.info("Trigger {} should execute for application {}",
                                trigger.getTriggerId(), application.getId());
                        executeTrigger(application, trigger);
                    } else {
                        logger.debug("Trigger {} not ready for application {}",
                                trigger.getTriggerId(), application.getId());
                    }
                }
            } else {
                logger.debug("No triggers found for status {}", application.getStatusId());
            }

            // Check LINK_CLICK and TEST_RESULT triggers for the next status
            Long nextStatusId = getNextStatusId(application);
            logger.debug("Next status for application {} is {}", application.getId(), nextStatusId);
            if (nextStatusId != null) {
                List<StatusTrigger> nextTriggers = statusTriggerRepository.findAllWithTriggerDetailsByStatus(nextStatusId);
                logger.debug("Checking {} triggers for next status {} for application {}",
                        nextTriggers.size(), nextStatusId, application.getId());
                for (StatusTrigger nextTrigger : nextTriggers) {
                    if ("LINK_CLICK".equals(nextTrigger.getTriggerType())) {
                        boolean triggerMatches = checkLinkClickedTrigger(application, nextTrigger);
                        logger.debug("Link click trigger {} for application {} on status {}: matches={}",
                                nextTrigger.getTriggerId(), application.getId(), nextStatusId, triggerMatches);
                        if (triggerMatches) {
                            logger.info("Link click trigger {} matches for application {}, moving to status {}",
                                    nextTrigger.getTriggerId(), application.getId(), nextStatusId);
                            executeTrigger(application, nextTrigger);
                        }
                    } else if ("TEST_RESULT".equals(nextTrigger.getTriggerType())) {
                        boolean triggerMatches = checkTestResultTrigger(application, nextTrigger);
                        logger.debug("Test result trigger {} for application {} on status {}: matches={}",
                                nextTrigger.getTriggerId(), application.getId(), nextStatusId, triggerMatches);
                        if (triggerMatches) {
                            logger.info("Test result trigger {} matches for application {}, moving to status {}",
                                    nextTrigger.getTriggerId(), application.getId(), nextStatusId);
                            executeTrigger(application, nextTrigger);
                        }
                    }
                }
            } else {
                logger.debug("No next status found for application {} with status {}",
                        application.getId(), application.getStatusId());
            }
        } catch (Exception e) {
            logger.error("Failed to check triggers for application {}: {}",
                    application.getId(), e.getMessage(), e);
        }
    }

    private Long getNextStatusId(Application application) {
        try {
            String sql = """
                SELECT id FROM application_statuses
                WHERE event_id = (SELECT event_id FROM applications WHERE id = ?)
                AND display_order > (SELECT display_order FROM application_statuses WHERE id = ?)
                ORDER BY display_order ASC
                LIMIT 1
            """;
            Long nextStatusId = jdbcTemplate.queryForObject(sql, new Object[]{application.getId(), application.getStatusId()}, Long.class);
            logger.debug("Fetched next status {} for application {}", nextStatusId, application.getId());
            return nextStatusId;
        } catch (Exception e) {
            logger.warn("No next status found for application {} and current status {}: {}",
                    application.getId(), application.getStatusId(), e.getMessage());
            return null;
        }
    }

    private Long getPreviousStatusId(Application application, Long triggerStatusId) {
        try {
            String sql = """
                SELECT id FROM application_statuses
                WHERE event_id = (SELECT event_id FROM applications WHERE id = ?)
                AND display_order < (SELECT display_order FROM application_statuses WHERE id = ?)
                ORDER BY display_order DESC
                LIMIT 1
            """;
            Long prevStatusId = jdbcTemplate.queryForObject(sql, new Object[]{application.getId(), triggerStatusId}, Long.class);
            logger.debug("Fetched previous status {} for application {} and trigger status {}",
                    prevStatusId, application.getId(), triggerStatusId);
            return prevStatusId;
        } catch (Exception e) {
            logger.warn("No previous status found for application {} and trigger status {}: {}",
                    application.getId(), triggerStatusId, e.getMessage());
            return null;
        }
    }

    private boolean shouldTrigger(Application application, StatusTrigger trigger) {
        if (statusTriggerRepository.isTriggerExecuted(
                application.getId(), trigger.getStatusId(), trigger.getTriggerId())) {
            logger.debug("Trigger {} already executed for application {}",
                    trigger.getTriggerId(), application.getId());
            return false;
        }

        boolean result = switch (trigger.getTriggerType()) {
            case "SEND_MESSAGE" -> true;
            case "LINK_CLICK" -> checkLinkClickedTrigger(application, trigger);
            case "TEST_RESULT" -> checkTestResultTrigger(application, trigger);
            default -> {
                logger.warn("Unknown trigger type: {}", trigger.getTriggerType());
                yield false;
            }
        };
        logger.debug("Trigger {} evaluation for application {}: {}",
                trigger.getTriggerId(), application.getId(), result);
        return result;
    }

    private boolean checkLinkClickedTrigger(Application application, StatusTrigger trigger) {
        Long previousStatusId = getPreviousStatusId(application, trigger.getStatusId());
        if (previousStatusId == null) {
            logger.debug("No previous status found for application {} and trigger status {}",
                    application.getId(), trigger.getStatusId());
            return false;
        }

        boolean isSent = robotRepository.isRobotTypeSent(
                application.getId(), previousStatusId, "SEND_MESSAGE_WITH_LINK");
        boolean isCallbackReceived = robotRepository.isCallbackReceived(
                application.getId(), previousStatusId, "LINK_CLICK");

        logger.debug("Checking LINK_CLICK trigger for application {} on previous status {}: isSent={}, isCallbackReceived={}",
                application.getId(), previousStatusId, isSent, isCallbackReceived);

        if (!isSent || !isCallbackReceived) {
            logger.warn("Trigger check failed for application {} on status {}: isSent={}, isCallbackReceived={}",
                    application.getId(), previousStatusId, isSent, isCallbackReceived);
            return false;
        }

        Map<String, Object> triggerParams = trigger.getParameters();
        String triggerLink = triggerParams.get("link") != null ? triggerParams.get("link").toString() : null;
        if (triggerLink == null) {
            logger.warn("No link provided in trigger {} parameters for application {}",
                    trigger.getTriggerId(), application.getId());
            return false;
        }

        String sql = """
            SELECT r.parameters->>'link' as link
            FROM application_robot_executions are
            JOIN robots r ON r.id = are.robot_id
            WHERE are.application_id = ?
            AND are.status_id = ?
            AND r.type = 'SEND_MESSAGE_WITH_LINK'
            AND are.executed = true
            AND are.callback_type = 'LINK_CLICK'
            ORDER BY are.executed_at DESC
            LIMIT 1
        """;
        try {
            String executedLink = jdbcTemplate.queryForObject(sql,
                    new Object[]{application.getId(), previousStatusId}, String.class);
            if (executedLink == null) {
                logger.warn("No link found in executed robot parameters for application {} on status {}",
                        application.getId(), previousStatusId);
                return false;
            }
            boolean linksMatch = triggerLink.equals(executedLink);
            logger.debug("Link comparison for application {} on status {}: triggerLink={}, executedLink={}, match={}",
                    application.getId(), previousStatusId, triggerLink, executedLink, linksMatch);
            if (!linksMatch) {
                logger.warn("Link mismatch for application {} on status {}: triggerLink={}, executedLink={}",
                        application.getId(), previousStatusId, triggerLink, executedLink);
            }
            return linksMatch;
        } catch (Exception e) {
            logger.error("Failed to fetch executed robot link for application {} on status {}: {}",
                    application.getId(), previousStatusId, e.getMessage());
            return false;
        }
    }

    private boolean checkTestResultTrigger(Application application, StatusTrigger trigger) {
        Long previousStatusId = getPreviousStatusId(application, trigger.getStatusId());
        if (previousStatusId == null) {
            logger.debug("No previous status found for application {} and trigger status {}",
                    application.getId(), trigger.getStatusId());
            return false;
        }

        boolean isTestSent = robotRepository.isRobotTypeSent(
                application.getId(), previousStatusId, "SEND_TEST");
        boolean isCallbackReceived = robotRepository.isCallbackReceived(
                application.getId(), previousStatusId, "TEST_PASSED");

        logger.debug("Test result trigger for application {} on previous status {}: isTestSent={}, isCallbackReceived={}",
                application.getId(), previousStatusId, isTestSent, isCallbackReceived);

        if (!isTestSent || !isCallbackReceived) {
            logger.warn("Test result trigger check failed for application {} on status {}: isTestSent={}, isCallbackReceived={}",
                    application.getId(), previousStatusId, isTestSent, isCallbackReceived);
            return false;
        }

        Map<String, Object> params = trigger.getParameters();
        String condition = params.get("condition") != null ? params.get("condition").toString() : null;
        Integer value = params.get("value") != null ? Integer.parseInt(params.get("value").toString()) : null;

        if (condition == null || value == null) {
            logger.warn("Invalid test parameters for trigger {}: condition={}, value={}",
                    trigger.getTriggerId(), condition, value);
            return false;
        }

        int testResult = getTestResult(application.getId());
        boolean result = switch (condition) {
            case "greater_than" -> testResult > value;
            case "less_than" -> testResult < value;
            case "equal" -> testResult == value;
            default -> {
                logger.warn("Unknown condition: {}", condition);
                yield false;
            }
        };
        logger.debug("Test result for application {}: result={}, condition={}, value={}, testResult={}",
                application.getId(), result, condition, value, testResult);
        return result;
    }

    private int getTestResult(Long applicationId) {
        logger.debug("Returning placeholder test result of 100 for application {}", applicationId);
        return 100;
    }

    private void executeTrigger(Application application, StatusTrigger trigger) throws TelegramApiException {
        Long targetStatusId = trigger.getStatusId();
        logger.info("Executing trigger {} for application {}, moving to status {}",
                trigger.getTriggerId(), application.getId(), targetStatusId);

        applicationRepository.updateStatus(application.getId(), targetStatusId);
        statusTriggerRepository.markTriggerExecuted(
                application.getId(), trigger.getStatusId(), trigger.getTriggerId());
        robotExecutorService.executeRobotsForStatus(application.getId(), targetStatusId);
    }
}
