package ru.katkova.flymerfindbot.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
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
import ru.katkova.flymerfindbot.handler.HandlerManagement;
import ru.katkova.flymerfindbot.service.FlymerMessageService;
import ru.katkova.flymerfindbot.service.FlymerReplyService;
import ru.katkova.flymerfindbot.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    public ReentrantLock lock = new ReentrantLock ();

    final Condition workCompleted = lock.newCondition();

    @Autowired
    private HandlerManagement handlerManagement;

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
        ru.katkova.flymerfindbot.data.User user;
        boolean isCommentChannel = false;
        boolean isChannel = false;
        //вставить проверку, на пустоту пользователя

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            user = userService.findInDB(chatId);
            if (chatId.equals(commentsChatId)) {
                isCommentChannel = true;
            }
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            user = userService.findInDB(chatId);
            isChannel = true;
            if (chatId.equals(commentsChatId)) {
                isCommentChannel = true;
            }
        }
        else {
            return;
        }

        if (!userService.existsInDB(chatId)) {
            if (isChannel && isCommentChannel) {

                String help = IOUtils.toString(this.getClass().getResourceAsStream("/description"),
                    StandardCharsets.UTF_8);

                SendMessage greetings = SendMessage.builder()
                        .chatId(chatId)
                        .text(help)
                        .build();
                execute(greetings);
            }
            String userName = update.getMessage().getChat().getUserName();
            userService.createNewUser(chatId, userName, isChannel, isCommentChannel);
        }

        if (!isChannel && !isCommentChannel) {
            List<PartialBotApiMethod<?>> methodList = handlerManagement.manage(user, update);
            for (PartialBotApiMethod<?> method: methodList) {
                if (method instanceof SendMessage) {
                    SendMessage sendMessage = (SendMessage) method;
                    execute(sendMessage);
                }
                else if (method instanceof SendPhoto) {
                    SendPhoto sendPhoto = (SendPhoto) method;
                    GetFile getFile = new GetFile(sendPhoto.getPhoto().getAttachName());
                    sendPhoto.setPhoto(new InputFile(getFile.getFileId()));
                    execute(sendPhoto);
                }
            }
        }

        if (isCommentChannel) {
            if (update.getMessage().getSenderChat() != null && update.getMessage().getChat() != null) {
                Integer telegramId = update.getMessage().getForwardFromMessageId();
                String mediagroup = update.getMessage().getMediaGroupId();
                Integer commentsId = update.getMessage().getMessageId();
                    try {
                        lock.lock();
                        FlymerMessage flymerMessage;
                        if (mediagroup != null) {
                            flymerMessage = flymerMessageService.findMessageByAdditionalIds(telegramId);
                        } else flymerMessage = flymerMessageService.findMessageByTelegramId(telegramId);
                        if (flymerMessage != null) {
                            flymerMessage.setCommentsId(commentsId);
                            flymerMessageService.save(flymerMessage);
                        } else {
                            if (update.getMessage().getText().contains("FlymerFindBot")) {
                                //do nothing
                            } else {
                                log.error("no db message with telegram id = " + telegramId);
                                if(update.getMessage().getCaption() != null)
                                    log.error(update.getMessage().getCaption());
                                else if (update.getMessage().getText() != null)
                                    log.error(update.getMessage().getText());
                            }
                        }
                        try {
                            workCompleted.signalAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
            }
        }
    }

    @SneakyThrows
    private void sendflymerMessageToChannel(ru.katkova.flymerfindbot.data.Message message)
            throws TelegramApiException {
            Long chatId = channelChatId;
            Long commentsId = commentsChatId;

            Predicate<Media> predicateImages = media -> (media.getType().equals("image"));
            if (message.getMediaList() == null || message.getMediaList().size() ==0 || ((message.getMediaList().size() >= 1
            && !(message.getMediaList().stream().filter(predicateImages).count() > 1)))) {
                PartialBotApiMethod<Message> telegramMessage = flymerMessageService.mapToTelegramMessage(message, chatId);
                if (telegramMessage instanceof SendMessage) {
                    SendMessage sendMessage = (SendMessage) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendMessage.setChatId(commentsId);
                        sendMessage.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                        Message telegramMessageSent = execute(sendMessage);
                        message.setTelegramId(telegramMessageSent.getMessageId());
                } else if (telegramMessage instanceof SendPhoto) {
                    SendPhoto sendPhoto = (SendPhoto) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendPhoto.setChatId(commentsId);
                        sendPhoto.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                    Message telegramMessageSent = execute(sendPhoto);
                    message.setTelegramId(telegramMessageSent.getMessageId());

                } else if (telegramMessage instanceof SendAnimation) {
                    SendAnimation sendAnimation = (SendAnimation) telegramMessage;
                    if (message instanceof FlymerReply) {
                        FlymerReply reply = (FlymerReply) message;
                        sendAnimation.setChatId(commentsId);
                        sendAnimation.setReplyToMessageId(reply.getFlymerMessage().getCommentsId());
                    }
                        Message telegramMessageSent = execute(sendAnimation);
                        message.setTelegramId(telegramMessageSent.getMessageId());
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
                List<Message> telegramMessageSent = execute(sendMediaGroup);
                List<String> idsList = telegramMessageSent.stream().map(a -> "'" + a.getMessageId()+"'").collect(Collectors.toList());
                message.setAdditionalIds(String.join(",", idsList.toString()));
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

        int messagesCounter = 0;

        for (FlymerMessage message : messageList) {
            FlymerMessage sentMessage = flymerMessageService.findMessage(message.getVkId(), message.getUserId());
            try {
                lock.lock();
                //не нашли в БД
                if (sentMessage == null) {
                    try {
                        sendflymerMessageToChannel(message);
                        message.setUpdated(true);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        log.error(message.getMessage());
                        continue;
                    }
                    try {
                        flymerMessageService.save(message);
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(message.getMessage());
                    }
                    messagesCounter++;
                    messagesCounter = doSleep(messagesCounter);
                    try {
                        while (flymerMessageService.findMessage(message.getVkId(), message.getUserId()).getCommentsId() == null) {
                            workCompleted.await();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }

            if (addReplies) {
                try {
                    lock.lock();

                    List<FlymerReply> flymerReplyList = message.getFlymerReplyList();

                    if (!(flymerReplyList == null || flymerReplyList.isEmpty())) {
                        flymerReplyList.sort(Comparator.comparing(FlymerReply::getDate));
                        Iterator<FlymerReply> replyIterator = flymerReplyList.iterator();
                        while (replyIterator.hasNext()) {
                            FlymerReply reply = replyIterator.next();
                            //ищем реплай в БД

                            sentMessage = flymerMessageService.findMessage(message.getVkId(), message.getUserId());
                            FlymerReply sentReply;
                            int replyIndex = sentMessage.getFlymerReplyList().indexOf(reply);
                            if (replyIndex == -1) {
                                sentReply = reply;
                                sentMessage.getFlymerReplyList().add(sentReply);
                            } else sentReply = sentMessage.getFlymerReplyList().get(replyIndex);


                            if (!sentReply.isUpdated()) {
                                sentReply.setFlymerMessage(sentMessage);
                                sendflymerMessageToChannel(sentReply);
                                sentReply.setUpdated(true);
                                try {
                                    flymerMessageService.save(sentMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error(message.getMessage());
                                }
                                messagesCounter++;
                                messagesCounter = doSleep(messagesCounter);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        }
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