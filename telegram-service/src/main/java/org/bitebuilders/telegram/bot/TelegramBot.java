package org.bitebuilders.telegram.bot;

import org.bitebuilders.telegram.config.BotConfig;
import org.bitebuilders.telegram.controller.CallbackQueryDispatcher;
import org.bitebuilders.telegram.bot.CommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final CommandDispatcher commandDispatcher;
    private final CallbackQueryDispatcher callbackQueryDispatcher;
    private final BotConfig botConfig;

    public TelegramBot(
            CommandDispatcher commandDispatcher,
            CallbackQueryDispatcher callbackQueryDispatcher,
            BotConfig botConfig) {
        super(botConfig.getToken());
        this.commandDispatcher = commandDispatcher;
        this.callbackQueryDispatcher = callbackQueryDispatcher;
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                logger.debug("Received message: {}", update.getMessage().getText());
                SendMessage response = commandDispatcher.dispatch(update.getMessage());
                execute(response);
            } else if (update.hasCallbackQuery()) { // <-- Это важно
                logger.debug("Received callback: {}", update.getCallbackQuery().getData());
                SendMessage response = callbackQueryDispatcher.dispatch(update.getCallbackQuery());
                execute(response);
            }
        } catch (TelegramApiException e) {
            logger.error("Failed to process update: {}", e.getMessage(), e);
        }
    }
}