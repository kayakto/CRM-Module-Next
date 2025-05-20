package org.bitebuilders.telegram.controller;

import org.bitebuilders.telegram.service.TelegramStateService;
import org.bitebuilders.telegram.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class DefaultCommandHandler implements CommandHandler {

    private final TelegramUserService telegramUserService;
    private final TelegramStateService stateService;

    public DefaultCommandHandler(TelegramUserService telegramUserService, TelegramStateService stateService) {
        this.telegramUserService = telegramUserService;
        this.stateService = stateService;
    }

    @Override
    public boolean supports(String command) {
        return true; // fallback handler
    }

    @Override
    public SendMessage handle(Message message) {
        return telegramUserService.handleOther(message);
    }
}

