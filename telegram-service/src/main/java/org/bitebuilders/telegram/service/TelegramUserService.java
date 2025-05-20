package org.bitebuilders.telegram.service;


import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.repository.ApplicationRepository;
import org.bitebuilders.telegram.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TelegramUserService {

    private final ApplicationRepository applicationRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramStateService stateService;

    public TelegramUserService(
            ApplicationRepository applicationRepository,
            TelegramUserRepository telegramUserRepository,
            TelegramStateService stateService
    ) {
        this.applicationRepository = applicationRepository;
        this.telegramUserRepository = telegramUserRepository;
        this.stateService = stateService;
    }

    public SendMessage handleStart(Message msg) {
        Long telegramId = msg.getFrom().getId();
        String telegramUsername = msg.getFrom().getUserName();
        String chatId = msg.getChatId().toString();

        String telegramUrl = "https://t.me/" + telegramUsername;
        List<Application> apps = applicationRepository.findAllByTelegramUrl(telegramUrl);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        if (apps.size() > 1) {
            message.setText("У вас несколько заявок. Выберите мероприятие, которое вас интересует:");

            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (Application application : apps) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("📅 Мероприятие ID " + application.getEventId());
                button.setCallbackData("select_event:" + application.getId()); // callbackData может быть ID заявки

                rows.add(List.of(button));
            }

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(rows);

            message.setReplyMarkup(markup);
        }
        return message;
    }

    /**
     * Обрабатывает все остальные сообщения (ожидание email и прочее)
     */
    public SendMessage handleOther(Message msg) {
        Long telegramId = msg.getFrom().getId();
        String telegramUsername = msg.getFrom().getUserName();
        String chatId = msg.getChatId().toString();
        String text = msg.getText().trim();

        if (stateService.isWaitingForEmail(telegramId)) {
            Optional<Application> application = applicationRepository.findByEmail(text);

            if (application.isPresent()) {
                Application app = application.get();
                applicationRepository.updateTelegramUrl(app.getId(), "https://t.me/" + telegramUsername);
                telegramUserRepository.saveIfNotExists(telegramId, telegramUsername, app.getId());
                stateService.clearWaiting(telegramId);

                return new SendMessage(chatId, "Спасибо! Вас нашли. Вы зарегистрированы на мероприятие: " + app.getEventId());
            } else {
                return new SendMessage(chatId, "Пользователь с таким email не найден. Попробуйте снова.");
            }
        }

        return new SendMessage(chatId, "Пожалуйста, введите команду /start.");
    }
}
