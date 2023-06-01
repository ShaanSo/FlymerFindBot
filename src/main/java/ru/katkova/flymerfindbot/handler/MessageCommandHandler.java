package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.CommandType;
import ru.katkova.flymerfindbot.data.Mode;
import ru.katkova.flymerfindbot.data.User;
import ru.katkova.flymerfindbot.data.UserAction;
import ru.katkova.flymerfindbot.service.UserService;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageCommandHandler implements UserActionHandler {

    private final String MESSAGE_HELP = "Введите сообщение, которое хотите отправить. К нему можно приложить одну картинку (используйте 'compress image', чтобы отправиьт картинку как фото, а не как файл). \n" +
            "Лимиты: для текста - 4048 символов, для текста с картинкой - 975." +
            "Если уже существует сохраненное, но не отправленное сообщение, то оно будет перезаписано.";

    @Autowired
    UserService userService;

    @Override
    public List<PartialBotApiMethod<?>> handle(User user, Update update) {
        List<PartialBotApiMethod<?>> sendMessageList = new ArrayList<>();
        userService.changeMode(user, Mode.SET_MESSAGE);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(user.getChatId())
                .text(MESSAGE_HELP)
                .build();
        sendMessageList.add(sendMessage);
        return sendMessageList;
    }

    @Override
    public UserAction getAction() {
        return CommandType.MESSAGE;
    }
}
