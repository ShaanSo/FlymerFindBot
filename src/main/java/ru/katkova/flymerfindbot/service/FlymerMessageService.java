package ru.katkova.flymerfindbot.service;

import com.vk.api.sdk.objects.widgets.CommentReplies;
import com.vk.api.sdk.objects.widgets.CommentRepliesItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import ru.katkova.flymerfindbot.data.FlymerReply;
import ru.katkova.flymerfindbot.repository.FlymerMessageRepository;
import ru.katkova.flymerfindbot.vkfix.WidgetCommentExt;
import java.util.*;

@Service
public class FlymerMessageService extends MessageService{

    @Autowired
    private FlymerMessageRepository flymerMessageRepository;

    public List<FlymerMessage> findAll() {
        return flymerMessageRepository.findAll();
    }

    public FlymerMessage mapFromWidget(WidgetCommentExt widgetMessage) {
        String userLogin = widgetMessage.getUser().getFirstName() + " " + widgetMessage.getUser().getLastName();
        Integer userId = widgetMessage.getUser().getId();
        FlymerMessage flymerMessage = FlymerMessage.builder()
                .vkId(widgetMessage.getId())
                .source(widgetMessage.getPostType())
                .date(widgetMessage.getDate())
                .message(widgetMessage.getText())
                .userLogin(userLogin)
                .userId(userId)
                .build();

        List<FlymerReply> flymerReplyList = new ArrayList<>();
        CommentReplies commentReplies = widgetMessage.getComments();
        if (commentReplies != null) {
            List<CommentRepliesItem> commentRepliesItemList = commentReplies.getReplies();
            for (CommentRepliesItem commentRepliesItem: commentRepliesItemList) {
                String replyLogin = commentRepliesItem.getUser().getFirstName() + " " + commentRepliesItem.getUser().getLastName();
                Integer replyUserId = commentRepliesItem.getUser().getId();
                FlymerReply flymerReply = FlymerReply.builder()
                        .vkId(commentRepliesItem.getCid())
                        .date(commentRepliesItem.getDate())
                        .message(commentRepliesItem.getText())
                        .userLogin(replyLogin)
                        .userId(replyUserId)
                        .vkPostId(userId.toString() + "_" + widgetMessage.getId().toString())
                        .build();
                flymerReplyList.add(flymerReply);
            }
            flymerMessage.setFlymerReplyList(flymerReplyList);
        }

        return flymerMessage;
    }

    public void save(FlymerMessage flymerMessage) {
        flymerMessageRepository.save(flymerMessage);
        if (flymerMessageRepository.count() >= 100) {
            flymerMessageRepository.delete(flymerMessageRepository.findFirstByOrderByDate());
        }
    }

    public FlymerMessage findMessageByTelegramId(Integer telegramId) {
        return flymerMessageRepository.findFirstByTelegramId(telegramId);
    }

    public FlymerMessage findMessage(Integer vkId, Integer userId) {
        return flymerMessageRepository.findFirstByVkIdAndUserId(vkId, userId);
    }

    public FlymerMessage findMessageByAdditionalIds(Integer id) {
        return flymerMessageRepository.findFirstByAdditionalIdsContains("'"+id+"'");
    }
}