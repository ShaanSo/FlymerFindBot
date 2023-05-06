//package ru.katkova.flymerfindbot.handler.callback;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import ru.katkova.flymerfindbot.data.CallbackType;
//import ru.katkova.flymerfindbot.data.Mode;
//import ru.katkova.flymerfindbot.data.User;
//import ru.katkova.flymerfindbot.data.UserAction;
//import ru.katkova.flymerfindbot.handler.UserActionHandler;
//import ru.katkova.flymerfindbot.service.UserService;
//
//@Service
//public class SetTextCallbackHandler implements UserActionHandler {
//
//    @Autowired
//    UserService userService;
//
//    @Override
//    public PartialBotApiMethod<?> handle(User user, Update update){
//        SendMessage message = SendMessage.builder()
//                .chatId(user.getChatId())
//                .text("Отправьте текст сообщения")
//                .build();
//        userService.changeMode(user, Mode.SET_TEXT);
//        return message;
//    }
//
//    @Override
//    public UserAction getAction() {
//        return CallbackType.TEXT;
//    }
//}
