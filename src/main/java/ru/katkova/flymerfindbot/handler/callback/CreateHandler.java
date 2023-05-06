//package ru.katkova.flymerfindbot.handler.callback;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
//import ru.katkova.flymerfindbot.data.CommandType;
//import ru.katkova.flymerfindbot.data.Mode;
//import ru.katkova.flymerfindbot.data.User;
//import ru.katkova.flymerfindbot.data.UserAction;
//import ru.katkova.flymerfindbot.handler.UserActionHandler;
//import ru.katkova.flymerfindbot.service.UserService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CreateHandler implements UserActionHandler {
//
//    @Autowired
//    UserService userService;
//
//    @Override
//    public PartialBotApiMethod<?> handle(User user, Update update) {
//
//        InlineKeyboardButton textButton = InlineKeyboardButton.builder()
//                        .text("Text").callbackData("text").build();
//        InlineKeyboardButton imageButton = InlineKeyboardButton.builder()
//                .text("Image").callbackData("image").build();
//        InlineKeyboardButton showButton = InlineKeyboardButton.builder()
//                .text("Show").callbackData("show").build();
//        InlineKeyboardButton sendButton = InlineKeyboardButton.builder()
//                .text("Send").callbackData("send").build();
//        InlineKeyboardButton cleanButton = InlineKeyboardButton.builder()
//                .text("Clean").callbackData("clean").build();
//
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<InlineKeyboardButton> firstRow = new ArrayList<>();
//        firstRow.add(textButton);
//        firstRow.add(imageButton);
//        firstRow.add(showButton);
//        List<InlineKeyboardButton> secondRow = new ArrayList<>();
//        secondRow.add(sendButton);
//        secondRow.add(cleanButton);
//        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//        rowList.add(firstRow);
//        rowList.add(secondRow);
//        inlineKeyboardMarkup.setKeyboard(rowList);
//
////        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
////        List<KeyboardRow> keyboardRowList = new ArrayList<>();
////        KeyboardRow keyboardRow1 = new KeyboardRow();
////        KeyboardRow keyboardRow2 = new KeyboardRow();
////        KeyboardRow keyboardRow3 = new KeyboardRow();
////        KeyboardRow keyboardRow4 = new KeyboardRow();
////        KeyboardRow keyboardRow5 = new KeyboardRow();
////        keyboardRow1.add("добавить текст");
////        keyboardRow2.add("добавить картинку");
////        keyboardRow3.add("отправить в канал");
////        keyboardRow4.add("предпросмотр");
////        keyboardRow5.add("очистить");
////        keyboardRowList.add(keyboardRow1);
////        keyboardRowList.add(keyboardRow2);
////        keyboardRowList.add(keyboardRow3);
////        keyboardRowList.add(keyboardRow4);
////        keyboardRowList.add(keyboardRow5);
////        replyKeyboardMarkup.setKeyboard(keyboardRowList);
////        replyKeyboardMarkup.setResizeKeyboard(true);
////        replyKeyboardMarkup.setOneTimeKeyboard(true);
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//        userService.changeMode(user, Mode.CREATE);
//        return sendMessage;
//    }
//
//    @Override
//    public UserAction getAction() {
//        return CommandType.CREATE;
//    }
//}
