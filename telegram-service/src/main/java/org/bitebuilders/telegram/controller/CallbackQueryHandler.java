package org.bitebuilders.telegram.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {
    boolean supports(String callbackData);
    SendMessage handle(CallbackQuery callbackQuery);
}
