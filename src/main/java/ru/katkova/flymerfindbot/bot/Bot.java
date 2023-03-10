package ru.katkova.flymerfindbot.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import ru.katkova.flymerfindbot.data.FlymerReply;
import ru.katkova.flymerfindbot.data.Media;
import ru.katkova.flymerfindbot.service.CommandService;
import ru.katkova.flymerfindbot.service.FlymerMessageService;
import ru.katkova.flymerfindbot.service.FlymerReplyService;
import ru.katkova.flymerfindbot.service.UserService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Getter
    @Value("${bot.name}")
    private final String BotUsername;
    @Getter
    @Value("${bot.token}")
    private final String BotToken;

    @Autowired
    private UserService userService;

    @Autowired
    private FlymerMessageService flymerMessageService;

    @Autowired
    private FlymerReplyService flymerReplyService;

    @Autowired
    private CommandService commandService;


//    @Autowired
//    private CheckForUpdates check;

    @Autowired
    RestTemplate restTemplate;

    @Value("${channel.chatId}")
    private Long channelChatId;

    @Value("${channel.commentsId}")
    private Long commentsChatId;

    @Value("${tg.messageCount}")
    private Long messageCount;

    @Value("${tg.minSleep}")
    private Long minSleep;

    @Value("${tg.maxSleep}")
    private Long maxSleep;

    private Semaphore sem = new Semaphore(1);
    private Semaphore replySem = new Semaphore(0);


    public Bot(
            TelegramBotsApi telegramBotsApi,
            @Value("${bot.name}") String botUsername,
            @Value("${bot.token}") String botToken) throws TelegramApiException {
        this.BotUsername = botUsername;
        this.BotToken = botToken;
        telegramBotsApi.registerBot(this);
    }

    @SneakyThrows
    public void onUpdateReceived(Update update) {
        Long chatId;
        boolean isCommentChannel = false;
        boolean isChannel;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            isChannel = false;
            if (chatId.equals(commentsChatId)) {
                isCommentChannel = true;
            }
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            isChannel = true;
            if (chatId.equals(commentsChatId)) {
                isCommentChannel = true;
            }
//        } else if (update.getMessage().getSenderChat() != null && update.getMessage().getChat() != null) {

        }
        else {
            return;
        }

        if (isCommentChannel) {
            if (update.getMessage().getSenderChat() != null && update.getMessage().getChat() != null) {
                Integer telegramId = update.getMessage().getForwardFromMessageId();
                Integer commentsId = update.getMessage().getMessageId();

                    try {
                        sem.acquire();
                        FlymerMessage flymerMessage = flymerMessageService.findMessageByTelegramId(telegramId);
                        if (flymerMessage == null) {
                            log.error("no db message");
                        }
                        flymerMessage.setCommentsId(commentsId);
                        flymerMessageService.save(flymerMessage);
                    } catch (Exception e) {
                        //
                    }
                    sem.release();
                    replySem.release();
            }
        }

        if (!userService.existsInDB(chatId)) {
            SendMessage greetings = SendMessage.builder()
                    .chatId(chatId)
                    .text("В работе.")
                    .build();
            Message greetingsMessage = execute(greetings);
            String userName = greetingsMessage.getChat().getUserName();
            userService.createNewUser(chatId, userName, isChannel);
        }

//        if (update.hasMessage() && update.getMessage().isCommand()) {
//            String command = update.getMessage().getText();
//            commandService.executeCommand(chatId, command);
//        }
    }

    private void sendflymerMessageToChannel(ru.katkova.flymerfindbot.data.Message message) {
            Long chatId = channelChatId;
            Long commentsId = commentsChatId;

            Predicate<Media> predicateImages = media -> (media.getType().equals("image"));
            if (message.getMediaList() == null || message.getMediaList().size() >= 1
            && message.getMediaList().stream().filter(predicateImages).count() == 1) {
                PartialBotApiMethod<Message> telegramMessage = flymerMessageService.mapToTelegramMessage(message, chatId);
                if (telegramMessage instanceof SendMessage) {
                    SendMessage sendMessage = (SendMessage) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendMessage.setChatId(commentsId);
                        sendMessage.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                    try {
                        Message telegramMessageSent = execute(sendMessage);
                        message.setTelegramId(telegramMessageSent.getMessageId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(sendMessage.getText());
                    }
                } else if (telegramMessage instanceof SendPhoto) {
                    SendPhoto sendPhoto = (SendPhoto) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendPhoto.setChatId(commentsId);
                        sendPhoto.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                        try {
                            Message telegramMessageSent = execute(sendPhoto);
                            message.setTelegramId(telegramMessageSent.getMessageId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(sendPhoto.getCaption());
                        }
                } else if (telegramMessage instanceof SendAnimation) {
                    SendAnimation sendAnimation = (SendAnimation) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendAnimation.setChatId(commentsId);
                        sendAnimation.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                    try {
                        Message telegramMessageSent = execute(sendAnimation);
                        message.setTelegramId(telegramMessageSent.getMessageId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(sendAnimation.getCaption());
                    }
                }
            } else if (message.getMediaList() != null && message.getMediaList().size() > 1
                    && message.getMediaList().stream().filter(predicateImages).count() > 1) {
                PartialBotApiMethod<ArrayList<Message>> telegramMessage = flymerMessageService.mapMediaListToTelegramMessage(message, chatId);
                SendMediaGroup sendMediaGroup = (SendMediaGroup) telegramMessage;
                if (message instanceof FlymerReply) {
                    FlymerReply reply = (FlymerReply) message;
                    sendMediaGroup.setChatId(commentsChatId);
                    sendMediaGroup.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                }
                    try {
                        List<Message> telegramMessageSent = execute(sendMediaGroup);
                        message.setTelegramId(telegramMessageSent.get(0).getMessageId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(sendMediaGroup.getMedias().get(0).getCaption());
                    }
            }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        if (updates.get(0).hasMyChatMember()) {
            ChatMemberUpdated chatMemberUpdated = updates.get(0).getMyChatMember();
            if (chatMemberUpdated.getNewChatMember().getStatus().equals("kicked")) {
                userService.deleteUser(chatMemberUpdated.getChat().getId());
            }
        } else updates.forEach(this::onUpdateReceived);
    }

    public void perform(List<ru.katkova.flymerfindbot.data.FlymerMessage> messageList, boolean addReplies) {
        flymerMessageService.fillMediaContent(messageList);
        List<FlymerMessage> sentMessageList = flymerMessageService.findAll();
        List<FlymerReply> sentRepliesList = flymerReplyService.findAll();
        sentMessageList.sort(Comparator.comparing(FlymerMessage::getDate));
        sentRepliesList.sort(Comparator.comparing(FlymerReply::getDate));
        int lastMessageTime = 0;
        int lastReplyTime = 0;
        if (!sentMessageList.isEmpty()) {
            lastMessageTime = sentMessageList.get(sentMessageList.size()-1).getDate();
            if (!sentRepliesList.isEmpty()) {
                lastReplyTime = sentRepliesList.get(sentRepliesList.size()-1).getDate();
            }
        }

        int messagesCounter = 0;

        for (FlymerMessage message : messageList) {
            if (message.getDate() > lastMessageTime) {
                try {
                    sem.acquire();
                } catch (Exception e) {
                    //
                }

                sendflymerMessageToChannel(message);
                messagesCounter++;
                messagesCounter = doSleep(messagesCounter);
                message.setUpdated(true);

                try {
                    flymerMessageService.save(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(message.getMessage());
                }
                try {
                    sem.release();
                } catch (Exception e) {
//
                }
            }

            if (addReplies) {
                try {
                    replySem.acquire();

                    //находим это сообщение в БД для апдейта
                    int index = sentMessageList.indexOf(message);
                    if (index == -1) log.debug(message.getMessage());
                    FlymerMessage sentMessage = sentMessageList.get(index);



                    if (message.getFlymerReplyList() == null) continue;
                    List<FlymerReply> flymerReplyList = message.getFlymerReplyList();

                    if (!flymerReplyList.isEmpty()) {
                        flymerReplyList.sort(Comparator.comparing(FlymerReply::getDate));
                        Iterator<FlymerReply> replyIterator = flymerReplyList.iterator();
                        while (replyIterator.hasNext()) {
                            FlymerReply reply = replyIterator.next();

                            int replyIndex = sentRepliesList.indexOf(reply);
                            if (replyIndex == -1) log.debug(reply.getMessage());
                            FlymerReply sentReply = sentRepliesList.get(replyIndex);

                            if (!sentReply.isUpdated()) {
                                reply.setFlymerMessage(sentMessage);
                                sendflymerMessageToChannel(reply);
                                reply.setUpdated(true);
                                sentMessage.getFlymerReplyList().add(reply);

                                try {
                                    reply.setUpdated(true);
                                    flymerMessageService.save(sentMessage);
                                } catch (Exception e) {
                                    log.error(sentMessage.getMessage());
                                }
                                messagesCounter++;
                                messagesCounter = doSleep(messagesCounter);
                            }
                        }
                    }
                } catch (Exception e) {
                    //
                }
                replySem.release();

            }

//            } else {
//                List<FlymerReply> flymerReplyList = message.getFlymerReplyList();
//                if (!flymerReplyList.isEmpty()) {
//                    flymerReplyList.sort(Comparator.comparing(FlymerReply::getDate));
//                    Iterator<FlymerReply> replyIterator = flymerReplyList.iterator();
//                    while (replyIterator.hasNext()) {
//                        FlymerReply reply = replyIterator.next();
//                        if (reply.getDate() > lastReplyTime) {
//                            reply.setFlymerMessage(message);
//                            sendflymerMessageToChannel(reply);
//                            messagesCounter++;
//                            messagesCounter = doSleep(messagesCounter);
//                        }
//                    }
//                }
//                try {
//                    sem.acquire();
//                } catch (Exception e) {
//                    //
//                }
//                try {
//                    flymerMessageService.save(message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.error(message.getMessage());
//                }
//                try {
//                    sem.release();
//                } catch (Exception e) {
//                    //
//                }
//            }
        }

//        for (FlymerMessage message : messageList) {
//            if (message.getDate() <= lastMessageTime && addReplies) {
//                //находим это сообщение в БД для апдейта
//                int index = sentMessageList.indexOf(message);
//                if (index == -1) log.debug(message.getMessage());
//                FlymerMessage sentMessage = sentMessageList.get(index);
//
//                if (message.getFlymerReplyList() == null) continue;
//                List<FlymerReply> flymerReplyList = message.getFlymerReplyList();
//
//                if (!flymerReplyList.isEmpty()) {
//                    flymerReplyList.sort(Comparator.comparing(FlymerReply::getDate));
//                    Iterator<FlymerReply> replyIterator = flymerReplyList.iterator();
//                    while (replyIterator.hasNext()) {
//                        FlymerReply reply = replyIterator.next();
//                        if (reply.getDate() > lastReplyTime) {
//                            reply.setFlymerMessage(sentMessage);
//                            sendflymerMessageToChannel(reply);
//                            sentMessage.setUpdated(true);
//                            sentMessage.getFlymerReplyList().add(reply);
//
//                            try {
//                                flymerMessageService.save(sentMessage);
//                            } catch (Exception e) {
//                                log.error(sentMessage.getMessage());
//                            }
//                            messagesCounter++;
//                            messagesCounter = doSleep(messagesCounter);
//                        }
//                    }
//                }
//            }
//        }

//        for (FlymerMessage toSaveMessage: sentMessageList) {
//            if (toSaveMessage.isUpdated()) {
//                try {
//                    flymerMessageService.save(toSaveMessage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.debug(toSaveMessage.getMessage());
//                }
//            }
//        }
    }

    private int doSleep(int messagesCounter){
        try {
            if (messagesCounter < messageCount) {
                Thread.sleep(minSleep);
            }
            else {
                Thread.sleep(maxSleep);
                messagesCounter = 0;
            }
        } catch (Exception e) {
            //do nothing
        }
        return messagesCounter;
    }
}