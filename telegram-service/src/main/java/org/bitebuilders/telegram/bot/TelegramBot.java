package org.bitebuilders.telegram.bot;

import org.bitebuilders.telegram.config.BotConfig;
import org.bitebuilders.telegram.controller.CallbackQueryDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CommandDispatcher dispatcher;
    private final CallbackQueryDispatcher callbackDispatcher;

    public TelegramBot(BotConfig botConfig, CommandDispatcher dispatcher, CallbackQueryDispatcher callbackDispatcher) {
        this.botConfig = botConfig;
        this.dispatcher = dispatcher;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage response = dispatcher.dispatch(update.getMessage());
            send(response);
        } else if (update.hasCallbackQuery()) {
            SendMessage response = callbackDispatcher.dispatch(update.getCallbackQuery());
            send(response);
        }
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}


