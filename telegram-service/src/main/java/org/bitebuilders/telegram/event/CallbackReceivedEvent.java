package org.bitebuilders.telegram.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CallbackReceivedEvent extends ApplicationEvent {
    private final String telegramId;
    private final String callbackData;
    private final Long applicationId;

    public CallbackReceivedEvent(Object source, String telegramId, String callbackData, Long applicationId) {
        super(source);
        this.telegramId = telegramId;
        this.callbackData = callbackData;
        this.applicationId = applicationId;
    }
}