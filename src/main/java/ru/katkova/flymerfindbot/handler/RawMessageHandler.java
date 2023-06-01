package ru.katkova.flymerfindbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import ru.katkova.flymerfindbot.service.RawMessageService;
import java.util.ArrayList;
import java.util.List;

@Service
public class RawMessageHandler implements UserActionHandler {

    @Autowired
    RawMessageService rawMessageService;

    @Override
    public List<PartialBotApiMethod<?>> handle(User user, Update update){

        String header = "<b>Автор: </b> FlymerFindBot\n" +
                "<b>Сообщение: </b>\n";

        List<PartialBotApiMethod<?>> sendMessageList = new ArrayList<>();
        RawMessage rawMessagefromBD = rawMessageService.findByChatId(user.getChatId());
        RawMessage rawMessage;
        if (rawMessagefromBD == null) {
            rawMessage = new RawMessage();
        } else rawMessage = rawMessagefromBD;
        String caption = "";
        String text = "";
        SendMessage sendMessage;
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            caption = header + (update.getMessage().getCaption() != null ? update.getMessage().getCaption() : "");
            rawMessage.setChatId(user.getChatId());
            rawMessage.setMessage(caption);
            Media media = new Media();
            media.setMediaUrl(update.getMessage().getPhoto().get(0).getFileId());
            List<Media> mediaList = new ArrayList<>();
            mediaList.add(media);
            rawMessage.setMediaList(mediaList);
        } else {
            text = header + (update.getMessage().getText() != null ? update.getMessage().getText() : "");
            rawMessage.setChatId(user.getChatId());
            rawMessage.setMessage(text);

        }
        if (caption.length() > 1024 || text.length() > 4096) {
            sendMessage = SendMessage.builder()
                    .text("Превышен лимит символов.")
                    .chatId(user.getChatId())
                    .build();
        } else {
            rawMessageService.save(rawMessage);
            sendMessage = SendMessage.builder()
                    .text("Сообщение сохранено. Чтобы просмотреть его используйте команду /show. Чтобы отправить его используйте команду /send.")
                    .chatId(user.getChatId())
                    .build();
        }
        sendMessageList.add(sendMessage);
        return sendMessageList;
    }

    @Override
    public UserAction getAction() {
        return OtherUserActionType.MESSAGE;
    }
}
