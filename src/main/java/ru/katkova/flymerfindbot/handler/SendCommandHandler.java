package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.RawMessageService;
import ru.katkova.flymerfindbot.service.UserService;

@Service
public class SendCommandHandler implements UserActionHandler{

    @Autowired
    UserService userService;

    @Autowired
    RawMessageService rawMessageService;

    private final static String header = "";

    @Value("${channel.chatId}")
    private Long channelChatId;

    @Override
    public PartialBotApiMethod<?> handle(User user, Update update) {
        RawMessage rawMessage = rawMessageService.findByChatId(user.getChatId());
        if (rawMessage == null) {
            SendMessage sendMessage = SendMessage.builder()
                    .text("Сообщение отсутствует в базе")
                    .chatId(user.getChatId())
                    .build();
            return sendMessage;
        } else {
            if (rawMessage.getMediaList() != null &&
                    !rawMessage.getMediaList().isEmpty() &&
                    !rawMessage.getMediaList().get(0).getMediaUrl().equals("")) {
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(channelChatId)
                        .photo(new InputFile(rawMessage.getMediaList().get(0).getMediaUrl()))
                        .caption(header + "\n" + rawMessage.getMessage())
                        .parseMode("html")
                        .build();
                userService.changeMode(user, Mode.NONE);
                rawMessageService.delete(rawMessage);
                return sendPhoto;
            } else {
                SendMessage sendMessage = SendMessage.builder()
                            .chatId(channelChatId)
                            .text(header + "\n" + rawMessage.getMessage())
                            .parseMode("html")
                            .build();
                    userService.changeMode(user, Mode.NONE);
                    rawMessageService.delete(rawMessage);
                return sendMessage;
            }
        }
    }

    @Override
    public UserAction getAction() {
        return CommandType.SEND;
    }
}
