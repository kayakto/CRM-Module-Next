package org.bitebuilders.telegram.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramStateService {
    private final Map<Long, Boolean> waitingEmail = new ConcurrentHashMap<>();

    public void setWaitingForEmail(Long telegramId) {
        waitingEmail.put(telegramId, true);
    }

    public boolean isWaitingForEmail(Long telegramId) {
        return waitingEmail.getOrDefault(telegramId, false);
    }

    public void clearWaiting(Long telegramId) {
        waitingEmail.remove(telegramId);
    }
}