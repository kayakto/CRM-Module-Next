package org.bitebuilders.telegram.controller;

import org.bitebuilders.telegram.event.CallbackReceivedEvent;
import org.bitebuilders.telegram.repository.TelegramUserRepository;
import org.bitebuilders.telegram.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(CallbackQueryDispatcher.class);

    private final TelegramUserRepository telegramUserRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TelegramUserService telegramUserService;

    public CallbackQueryDispatcher(
            TelegramUserRepository telegramUserRepository,
            ApplicationEventPublisher eventPublisher,
            TelegramUserService telegramUserService) {
        this.telegramUserRepository = telegramUserRepository;
        this.eventPublisher = eventPublisher;
        this.telegramUserService = telegramUserService;
    }

    public SendMessage dispatch(CallbackQuery callbackQuery) {
        String telegramId = callbackQuery.getFrom().getId().toString();
        String callbackData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();

        logger.debug("Dispatching callback for telegramId: {}, data: {}", telegramId, callbackData);

        // Handle both select_event and LINK_CLICK callbacks in TelegramUserService
        if (callbackData.startsWith("select_event:") || callbackData.startsWith("LINK_CLICK:")
                || callbackData.startsWith("TEST_PASSED:")) {
            return telegramUserService.handleCallback(callbackQuery);
        }

        try {
            Long applicationId = telegramUserRepository.findApplicationIdByTelegramId(telegramId);
            if (applicationId == null) {
                logger.warn("No application found for telegram ID: {}", telegramId);
                return new SendMessage(chatId, "Ошибка: ваша заявка не найдена. Используйте /start.");
            }

            // Publish event for other callback types (e.g., TEST_PASSED)
            eventPublisher.publishEvent(new CallbackReceivedEvent(this, telegramId, callbackData, applicationId));
            return new SendMessage(chatId, "Действие успешно обработано!");
        } catch (Exception e) {
            logger.error("Failed to process callback {} for telegram ID {}: {}", callbackData, telegramId, e.getMessage(), e);
            return new SendMessage(chatId, "Произошла ошибка при обработке действия.");
        }
    }
}