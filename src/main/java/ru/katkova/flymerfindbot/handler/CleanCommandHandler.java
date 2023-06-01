package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.RawMessageService;
import ru.katkova.flymerfindbot.service.UserService;
import java.util.ArrayList;
import java.util.List;

@Service
public class CleanCommandHandler implements UserActionHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private RawMessageService rawMessageService;
    @Override
    public List<PartialBotApiMethod<?>> handle(User user, Update update){
        List<PartialBotApiMethod<?>> sendMessageList = new ArrayList<>();
        SendMessage sendMessage = SendMessage.builder()
                .text("Сообщение удалено из базы")
                .chatId(user.getChatId())
                .build();
        sendMessageList.add(sendMessage);
        RawMessage rawMessage = rawMessageService.findByChatId(user.getChatId());
        if (rawMessage != null) {
            rawMessageService.delete(rawMessage);
            userService.changeMode(user, Mode.NONE);
        }
        return sendMessageList;
    }

    @Override
    public UserAction getAction() {
        return CommandType.CLEAN;
    }
}
