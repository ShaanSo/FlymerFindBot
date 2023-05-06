package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.RawMessageService;

@Service
public class ShowCommandHandler implements UserActionHandler {

    @Autowired
    RawMessageService rawMessageService;

    @Override
    public PartialBotApiMethod<?> handle(User user, Update update){

        RawMessage rawMessage = rawMessageService.findByChatId(user.getChatId());
        if (rawMessage == null) {
            SendMessage sendMessage = SendMessage.builder()
                    .text("Сообщение отсутствует в базе")
                    .chatId(user.getChatId())
                    .build();
            return sendMessage;
        } else {
            if (!rawMessage.getMediaList().isEmpty()) {
                SendPhoto sendPhoto = SendPhoto.builder()
                        .caption(rawMessage.getMessage() )
                        .photo(new InputFile(rawMessage.getMediaList().get(0).getMediaUrl()))
                        .chatId(user.getChatId())
                        .parseMode("html")
                        .build();
                return sendPhoto;
            } else {
                SendMessage sendMessage = SendMessage.builder()
                            .text(rawMessage.getMessage())
                            .chatId(user.getChatId())
                            .parseMode("html")
                            .build();
                return sendMessage;
            }
        }
    }

    @Override
    public UserAction getAction() {
        return CommandType.SHOW;
    }
}
