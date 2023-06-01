package ru.katkova.flymerfindbot.handler;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultHandler implements UserActionHandler{

    @Autowired
    UserService userService;

    @Override
    @SneakyThrows
    public List<PartialBotApiMethod<?>> handle(User user, Update update){
        List<PartialBotApiMethod<?>> sendMessageList = new ArrayList<>();

        String help = IOUtils.toString(this.getClass().getResourceAsStream("/description"),
            StandardCharsets.UTF_8);

        SendMessage sendMessage = SendMessage.builder()
                .text(help)
                .chatId(user.getChatId())
                .build();
        sendMessageList.add(sendMessage);
        userService.changeMode(user, Mode.NONE);
        return sendMessageList;
    }

    @Override
    public UserAction getAction() {
        return OtherUserActionType.HELP;
    }

}
