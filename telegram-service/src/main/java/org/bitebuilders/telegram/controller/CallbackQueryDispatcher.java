package org.bitebuilders.telegram.controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class CallbackQueryDispatcher {

    private final List<CallbackQueryHandler> handlers;

    public CallbackQueryDispatcher(List<CallbackQueryHandler> handlers) {
        this.handlers = handlers;
    }

    public SendMessage dispatch(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData().trim();
        return handlers.stream()
                .filter(handler -> handler.supports(data))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No handler found"))
                .handle(callbackQuery);
    }
}

