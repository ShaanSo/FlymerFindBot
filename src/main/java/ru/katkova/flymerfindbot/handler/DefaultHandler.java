package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.UserService;

@Service
public class DefaultHandler implements UserActionHandler{

    private final String HELP = "Бот предназначен для отправки сообщений в канал FlymerFindChannel." +
            "Помощь по командам: \n" +
            "/message - сформировать сообщение в бота\n" +
            "/show - предпросмотр сообщения\n" +
            "/clean - очистить сообщение\n" +
            "/send - отправить сообщение в канал\n" +
            "/help - помощь по командам";

    @Autowired
    UserService userService;

    @Override
    public PartialBotApiMethod<?> handle(User user, Update update){
        SendMessage sendMessage = SendMessage.builder()
                .text(HELP)
                .chatId(user.getChatId())
                .build();
        userService.changeMode(user, Mode.NONE);
        return sendMessage;
    }

    @Override
    public UserAction getAction() {
        return OtherUserActionType.HELP;
    }

}
