package org.bitebuilders.telegram.controller;

import org.bitebuilders.telegram.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StartCommandHandler.class);

    private final TelegramUserService telegramUserService;

    public StartCommandHandler(TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
    }

    @Override
    public boolean supports(String command) {
        return "/start".equalsIgnoreCase(command.trim());
    }

    @Override
    public SendMessage handle(Message message) {
        logger.debug("Handling /start command for chatId: {}", message.getChatId());
        return telegramUserService.handleStart(message);
    }
}