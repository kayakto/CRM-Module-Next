package org.bitebuilders.telegram.service;

import org.bitebuilders.telegram.bot.TelegramBot;
import org.bitebuilders.telegram.event.ApplicationSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ApplicationSelectedListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationSelectedListener.class);

    private final RobotExecutorService robotExecutorService;
    private final TelegramBot telegramBot;

    public ApplicationSelectedListener(RobotExecutorService robotExecutorService,
                                       TelegramBot telegramBot) {
        this.robotExecutorService = robotExecutorService;
        this.telegramBot = telegramBot;
    }

    @EventListener
    public void handleApplicationSelected(ApplicationSelectedEvent event) {
        Long applicationId = event.getApplicationId();
        Long statusId = event.getStatusId();
        String telegramId = event.getTelegramId();
        String chatId = event.getChatId();

        logger.info("Handling application selected event for application {}, status {}, telegramId {}",
                applicationId, statusId, telegramId);

        try {
            // Выполняем роботов для текущего статуса
            robotExecutorService.executeRobotsForStatus(applicationId, statusId);

            // Отправляем подтверждение пользователю
            SendMessage message = new SendMessage(chatId,
                    "Вы выбрали мероприятие. Ожидайте дальнейших уведомлений.");
            telegramBot.execute(message);
            logger.debug("Sent confirmation message to chatId {}", chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to execute robots or send message for application {}: {}",
                    applicationId, e.getMessage(), e);
        }
    }
}