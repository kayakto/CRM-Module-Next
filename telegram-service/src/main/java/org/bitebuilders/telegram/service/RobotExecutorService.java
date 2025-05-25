package org.bitebuilders.telegram.service;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.telegram.bot.TelegramBot;
import org.bitebuilders.telegram.model.Robot;
import org.bitebuilders.telegram.repository.ApplicationRepository;
import org.bitebuilders.telegram.repository.RobotRepository;
import org.bitebuilders.telegram.repository.TelegramUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RobotExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(RobotExecutorService.class);

    private final TelegramBot telegramBot;
    private final RobotRepository robotRepository;
    private final TelegramUserRepository telegramUserRepository;

    public void executeRobotsForStatus(Long applicationId, Long statusId) throws TelegramApiException {
        String telegramId = telegramUserRepository.findTelegramIdByApplicationId(applicationId);
        if (telegramId == null) {
            logger.warn("Telegram ID not found for application: {}", applicationId);
            return;
        }

        List<Robot> robots = robotRepository.fetchRobotsForStatus(statusId);
        logger.debug("Found {} robots for status {}", robots.size(), statusId);

        for (Robot robot : robots) {
            if (!robotRepository.isRobotTypeSent(applicationId, statusId, robot.getType())) {
                try {
                    executeRobot(applicationId, statusId, telegramId, robot);
                } catch (RuntimeException e) {
                    logger.error("Skipping robot {} execution for application {} and status {} due to database error: {}",
                            robot.getId(), applicationId, statusId, e.getMessage());
                    try {
                        robotRepository.markRobotSent(applicationId, statusId, robot.getId());
                    } catch (Exception ex) {
                        logger.error("Failed to mark robot {} as sent after error for application {} and status {}: {}",
                                robot.getId(), applicationId, statusId, ex.getMessage());
                    }
                }
            } else {
                logger.debug("Robot {} already sent for application {} and status {}",
                        robot.getId(), applicationId, statusId);
            }
        }
    }

    private void executeRobot(Long applicationId, Long statusId, String telegramId, Robot robot)
            throws TelegramApiException {
        logger.debug("Executing robot {} for application {} and status {}",
                robot.getId(), applicationId, statusId);

        Map<String, Object> params = robot.getParameters();
        String robotType = robot.getType();

        try {
            switch (robotType) {
                case "SEND_MESSAGE_WITH_LINK":
                    String msg = params.get("message") != null ? params.get("message").toString()
                            : "Please follow the link: ";
                    String link = params.get("link") != null ? params.get("link").toString() : null;

                    if (link == null || link.isEmpty()) {
                        logger.warn("Missing or empty URL in parameters for telegramId {}", telegramId);
                        return;
                    }

                    SendMessage messageWithLink = new SendMessage();
                    messageWithLink.setChatId(telegramId);
                    messageWithLink.setText(msg + " Ссылка: " + link);

                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText("Открыл(а) ссылку");
                    button.setCallbackData("LINK_CLICK:" + applicationId + ":" + statusId + ":" + robot.getId());
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    markup.setKeyboard(List.of(List.of(button)));
                    messageWithLink.setReplyMarkup(markup);

                    telegramBot.execute(messageWithLink);
                    logger.debug("Sent message with button to telegramId {}: {}", telegramId, messageWithLink);

                    robotRepository.markRobotSent(applicationId, statusId, robot.getId());
                    logger.info("Sent robot {} for application {} and status {}", robot.getId(), applicationId, statusId);
                    break;

                case "SEND_MESSAGE":
                    String text = params.get("message") != null ? params.get("message").toString()
                            : "Default message";

                    SendMessage message = new SendMessage();
                    message.setChatId(telegramId);
                    message.setText(text);

                    telegramBot.execute(message);
                    logger.debug("Sent message to telegramId {}: {}", telegramId, text);

                    robotRepository.markRobotExecuted(applicationId, statusId, robot.getId(), null);
                    logger.info("Executed robot {} for application {} and status {}", robot.getId(), applicationId, statusId);
                    break;

                case "SEND_TEST":
                    String testMsg = params.get("message") != null ? params.get("message").toString()
                            : "Please complete the test.";
                    String linkTest = params.get("link") != null ? params.get("link").toString() : null;

                    SendMessage testMessage = new SendMessage();
                    testMessage.setChatId(telegramId);
                    testMessage.setText(testMsg + (linkTest != null ? " Ссылка на тест: " + linkTest : ""));

                    InlineKeyboardButton buttonTest = new InlineKeyboardButton();
                    buttonTest.setText("Закончил(а) тестирование");
                    buttonTest.setCallbackData("TEST_PASSED:" + applicationId + ":" + statusId + ":" + robot.getId());

                    InlineKeyboardMarkup markupTest = new InlineKeyboardMarkup();
                    markupTest.setKeyboard(List.of(List.of(buttonTest)));
                    testMessage.setReplyMarkup(markupTest);

                    telegramBot.execute(testMessage);
                    logger.debug("Sent test message to telegramId {}: {}", telegramId, testMsg);

                    robotRepository.markRobotSent(applicationId, statusId, robot.getId());
                    logger.info("Sent test robot {} for application {} and status {}", robot.getId(), applicationId, statusId);
                    break;

                default:
                    logger.warn("Unsupported robot type: {}", robotType);
            }
        } catch (TelegramApiException e) {
            logger.error("Failed to execute robot {} for application {}: {}", robot.getId(), applicationId, e.getMessage(), e);
            throw e;
        }
    }
}