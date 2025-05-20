package org.bitebuilders.telegram.controller;

import org.bitebuilders.telegram.model.Application;
import org.bitebuilders.telegram.repository.ApplicationRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Optional;

@Component
public class SelectEventCallbackHandler implements CallbackQueryHandler {

    private final ApplicationRepository applicationRepository;

    public SelectEventCallbackHandler(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("select_event:");
    }

    @Override
    public SendMessage handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long applicationId = Long.parseLong(data.split(":")[1]);
        Long chatId = callbackQuery.getMessage().getChatId();

        Optional<Application> appOpt = applicationRepository.findById(applicationId);

        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            return new SendMessage(chatId.toString(),
                    "Вы выбрали мероприятие ID: " + app.getEventId() + "\n\n(тут можно продолжить взаимодействие)");
        } else {
            return new SendMessage(chatId.toString(), "Не удалось найти заявку. Попробуйте снова.");
        }
    }
}

