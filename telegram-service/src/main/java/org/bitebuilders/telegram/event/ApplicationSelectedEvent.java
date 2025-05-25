package org.bitebuilders.telegram.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApplicationSelectedEvent extends ApplicationEvent {
    private final Long applicationId;
    private final Long statusId;
    private final String telegramId;
    private final String chatId;

    public ApplicationSelectedEvent(Object source, Long applicationId, Long statusId,
                                    String telegramId, String chatId) {
        super(source);
        this.applicationId = applicationId;
        this.statusId = statusId;
        this.telegramId = telegramId;
        this.chatId = chatId;
    }
}