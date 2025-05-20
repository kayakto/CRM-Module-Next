package org.bitebuilders.telegram.bot;

import org.bitebuilders.telegram.controller.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CommandDispatcher {

    private final List<CommandHandler> handlers;

    public CommandDispatcher(List<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    public SendMessage dispatch(Message message) {
        String text = message.getText().trim();
        return handlers.stream()
                .filter(handler -> handler.supports(text))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No handler found"))
                .handle(message);
    }
}

