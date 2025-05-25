package org.bitebuilders.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    private final TelegramBot telegramBot;

    public BotInitializer(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
            logger.info("Telegram bot {} successfully registered", telegramBot.getBotUsername());
        } catch (Exception e) {
            logger.error("Failed to register Telegram bot: {}", e.getMessage(), e);
            throw e;
        }
    }
}

