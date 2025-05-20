package org.bitebuilders.telegram.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {
    boolean supports(String command);
    SendMessage handle(Message message);
}
