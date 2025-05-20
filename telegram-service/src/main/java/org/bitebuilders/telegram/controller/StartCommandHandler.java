package org.bitebuilders.telegram.controller;

import org.bitebuilders.telegram.service.ApplicationService;
import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.service.TelegramStateService;
import org.bitebuilders.telegram.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommandHandler implements CommandHandler {

    private final TelegramUserService telegramUserService;

    public StartCommandHandler(TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
    }

    @Override
    public boolean supports(String command) {
        return "/start".equalsIgnoreCase(command);
    }

    @Override
    public SendMessage handle(Message message) {
        return telegramUserService.handleStart(message);
    }
}

