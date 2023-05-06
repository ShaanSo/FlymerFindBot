//package ru.katkova.flymerfindbot.handler;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import ru.katkova.flymerfindbot.data.*;
//import ru.katkova.flymerfindbot.service.UserService;
//
//@Service
//public class SetTextCommandHandler implements UserActionHandler{
//
//    @Autowired
//    UserService userService;
//
//    @Override
//    public PartialBotApiMethod<?> handle(User user, Update update) {
//        userService.changeMode(user, Mode.SET_TEXT);
//        SendMessage sendMessage = SendMessage.builder()
//                .chatId(user.getChatId())
//                .text("Введите текст, который хотите отправить:")
//                .build();
//        return sendMessage;
//    }
//
//    @Override
//    public UserAction getAction() {
//        return CommandType.SET_TEXT;
//    }
//}
