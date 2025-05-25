package org.bitebuilders.telegram.service;

import org.bitebuilders.telegram.event.ApplicationSelectedEvent;
import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.repository.ApplicationRepository;
import org.bitebuilders.telegram.repository.RobotRepository;
import org.bitebuilders.telegram.repository.TelegramUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramUserService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramUserService.class);

    private final ApplicationRepository applicationRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramStateService stateService;
    private final ApplicationEventPublisher eventPublisher;
    private final RobotRepository robotRepository;

    public TelegramUserService(
            ApplicationRepository applicationRepository,
            TelegramUserRepository telegramUserRepository,
            TelegramStateService stateService,
            ApplicationEventPublisher eventPublisher,
            RobotRepository robotRepository) {
        this.applicationRepository = applicationRepository;
        this.telegramUserRepository = telegramUserRepository;
        this.stateService = stateService;
        this.eventPublisher = eventPublisher;
        this.robotRepository = robotRepository;
    }

    public SendMessage handleStart(Message msg) {
        Long telegramId = msg.getFrom().getId();
        String telegramUsername = msg.getFrom().getUserName();
        String chatId = msg.getChatId().toString();

        logger.debug("Processing /start for telegramId: {}, username: {}", telegramId, telegramUsername);

        String telegramUrl = "https://t.me/" + (telegramUsername != null ? telegramUsername : telegramId);
        logger.debug("Attempting to find applications for telegramUrl: {}", telegramUrl);
        List<Application> apps = applicationRepository.findAllByTelegramUrl(telegramUrl);
        logger.info("Found {} applications for telegramUrl {}: {}", apps.size(), telegramUrl, apps.stream().map(Application::getId).toList());

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (apps.isEmpty()) {
            logger.warn("No applications found for telegramUrl: {}", telegramUrl);
            message.setText("У вас нет активных заявок. Пожалуйста, зарегистрируйтесь на мероприятие.");
            return message;
        }

        if (apps.size() > 1) {
            logger.info("Found {} applications for telegramUrl: {}", apps.size(), telegramUrl);
            message.setText("У вас несколько заявок. Выберите мероприятие, которое вас интересует:");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (Application app : apps) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                String eventName = app.getEventId() != null ? "Мероприятие ID: " + app.getEventId() : "Заявка ID: " + app.getId();
                button.setText(eventName);
                button.setCallbackData("select_event:" + app.getId());
                keyboard.add(List.of(button));
            }

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);
            message.setReplyMarkup(markup);
        } else {
            Application application = apps.get(0);
            if (application.getEventId() == null) {
                logger.warn("Application {} has no event_id for telegramUrl: {}", application.getId(), telegramUrl);
                message.setText("Ошибка: ваша заявка не привязана к мероприятию. Обратитесь в поддержку.");
                return message;
            }
            logger.info("Found single application for telegramUrl: {}, eventId: {}, applicationId: {}",
                    telegramUrl, application.getEventId(), application.getId());
            telegramUserRepository.saveTelegramUser(telegramId.toString(), telegramUsername,
                    application.getId());
            logger.debug("Called saveTelegramUser for telegramId: {}, applicationId: {}",
                    telegramId, application.getId());
            eventPublisher.publishEvent(new ApplicationSelectedEvent(this, application.getId(),
                    application.getStatusId(), telegramId.toString(), chatId));
            message.setText("Вы зарегистрированы на мероприятие ID: " + application.getEventId() +
                    ". Ожидайте дальнейших уведомлений.");
        }

        return message;
    }

    public SendMessage handleCallback(CallbackQuery callbackQuery) {
        String telegramId = callbackQuery.getFrom().getId().toString();
        String telegramUsername = callbackQuery.getFrom().getUserName();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callbackData = callbackQuery.getData();

        logger.info("Received callback for telegramId: {}, data: {}", telegramId, callbackData);

        if (callbackData == null || callbackData.isEmpty()) {
            logger.warn("Empty callback data received for telegramId: {}", telegramId);
            return new SendMessage(chatId, "Ошибка: пустое действие. Попробуйте снова.");
        }

        if (callbackData.startsWith("select_event:")) {
            try {
                Long applicationId = Long.parseLong(callbackData.split(":")[1]);
                Application application = applicationRepository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

                if (application.getEventId() == null) {
                    logger.warn("Selected application {} has no event_id for telegramId: {}", applicationId, telegramId);
                    return new SendMessage(chatId, "Ошибка: выбранная заявка не привязана к мероприятию. Обратитесь в поддержку.");
                }

                telegramUserRepository.saveTelegramUser(telegramId, telegramUsername, applicationId);
                logger.debug("Called saveTelegramUser for telegramId: {}, applicationId: {}",
                        telegramId, applicationId);
                eventPublisher.publishEvent(new ApplicationSelectedEvent(this, applicationId,
                        application.getStatusId(), telegramId, chatId));

                return new SendMessage(chatId, "Вы выбрали мероприятие ID: " + application.getEventId() +
                        ". Ожидайте дальнейших уведомлений.");
            } catch (Exception e) {
                logger.error("Failed to process select_event callback for telegramId {}: {}", telegramId, e.getMessage(), e);
                return new SendMessage(chatId, "Ошибка при выборе мероприятия. Попробуйте снова.");
            }
        } else if (callbackData.startsWith("LINK_CLICK:")) {
            try {
                String[] parts = callbackData.split(":");
                if (parts.length != 4) {
                    logger.warn("Invalid LINK_CLICK callback format: {}", callbackData);
                    return new SendMessage(chatId, "Ошибка обработки действия.");
                }
                Long applicationId = Long.parseLong(parts[1]);
                Long statusId = Long.parseLong(parts[2]);
                Long robotId = Long.parseLong(parts[3]);

                logger.debug("Processing LINK_CLICK for application {}, status {}, robot {}", applicationId, statusId, robotId);

                boolean isSent = robotRepository.isRobotTypeSent(applicationId, statusId, "SEND_MESSAGE_WITH_LINK");
                if (!isSent) {
                    logger.warn("No execution record found for application {}, status {}, robot {}", applicationId, statusId, robotId);
                    return new SendMessage(chatId, "Ошибка: действие не найдено. Попробуйте снова.");
                }

                robotRepository.markCallbackReceived(applicationId, statusId, robotId, "LINK_CLICK");
                logger.info("Successfully marked LINK_CLICK callback for application {} on status {} for robot {}", applicationId, statusId, robotId);

                return new SendMessage(chatId, "Действие успешно обработано!");
            } catch (Exception e) {
                logger.error("Failed to process LINK_CLICK callback for telegramId {}: {}", telegramId, e.getMessage(), e);
                return new SendMessage(chatId, "Ошибка при активации ссылки. Попробуйте снова.");
            }
        } else if (callbackData.startsWith("TEST_PASSED:")) {
            try {
                String[] parts = callbackData.split(":");
                if (parts.length != 4) {
                    logger.warn("Invalid TEST_PASSED callback format: {}", callbackData);
                    return new SendMessage(chatId, "Ошибка обработки действия.");
                }
                Long applicationId = Long.parseLong(parts[1]);
                Long statusId = Long.parseLong(parts[2]);
                Long robotId = Long.parseLong(parts[3]);

                logger.debug("Processing TEST_PASSED for application {}, status {}, robot {}", applicationId, statusId, robotId);

                boolean isSent = robotRepository.isRobotTypeSent(applicationId, statusId, "SEND_TEST");
                if (!isSent) {
                    logger.warn("No execution record found for application {}, status {}, robot {}", applicationId, statusId, robotId);
                    return new SendMessage(chatId, "Ошибка: тест не найден. Попробуйте снова.");
                }

                // Use markCallbackReceived instead of markRobotExecuted
                robotRepository.markCallbackReceived(applicationId, statusId, robotId, "TEST_PASSED");
                logger.info("Successfully marked TEST_PASSED callback for application {} on status {} for robot {}", applicationId, statusId, robotId);

                return new SendMessage(chatId, "Тест успешно завершен! Ваш результат: 100 баллов.");
            } catch (Exception e) {
                logger.error("Failed to process TEST_PASSED callback for telegramId {}: {}", telegramId, e.getMessage(), e);
                return new SendMessage(chatId, "Ошибка при завершении теста. Попробуйте снова.");
            }
        }

        logger.warn("Unknown callback data: {} for telegramId: {}", callbackData, telegramId);
        return new SendMessage(chatId, "Неизвестное действие. Используйте /start.");
    }
}