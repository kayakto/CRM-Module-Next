package org.bitebuilders.telegram.bot;

import org.bitebuilders.telegram.controller.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CommandDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(CommandDispatcher.class);

    private final List<CommandHandler> handlers;

    public CommandDispatcher(List<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    public SendMessage dispatch(Message message) {
        String text = message.getText().trim();
        logger.debug("Dispatching command: {}", text);
        return handlers.stream()
                .filter(handler -> handler.supports(text))
                .findFirst()
                .map(handler -> {
                    logger.debug("Found handler: {}", handler.getClass().getSimpleName());
                    return handler.handle(message);
                })
                .orElseGet(() -> {
                    logger.warn("No handler found for command: {}", text);
                    return new SendMessage(message.getChatId().toString(),
                            "Команда не распознана. Используйте /start для начала.");
                });
    }
}