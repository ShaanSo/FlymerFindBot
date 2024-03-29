package ru.katkova.flymerfindbot.service;

import org.jvnet.hk2.annotations.Service;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import ru.katkova.flymerfindbot.data.FlymerReply;
import ru.katkova.flymerfindbot.data.Media;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaService {

    public void addImagesAndVideosToMessage(List<Media> mediaList, List<FlymerMessage> flymerMessageList, String postId) {
        boolean isFound = false;
        loop: for (FlymerMessage message: flymerMessageList) {
            if (message.getMessage().equals("Post deleted ")) break;
            String postIdFlymer = "wpt" + message.getUserId().toString() + "_" + message.getVkId().toString();
            if (postIdFlymer.equals(postId)) {
                message.setMediaList(mediaList);
                isFound = true;
                break;
            }
            for (FlymerReply reply: message.getFlymerReplyList()) {
                String replyIdFlymer = "wpt" + message.getUserId().toString() + "_" + reply.getVkId().toString();
                if (replyIdFlymer.equals(postId)) {
                    reply.setMediaList(mediaList);
                    isFound = true;
                    break loop;
                }
            }
        }
        if (!isFound) log.error("No message found for media, post id = " + postId);
    }

    public void addMediaToMessage(Media media, List<FlymerMessage> flymerMessageList, String postId) {
        loop: for (FlymerMessage message: flymerMessageList) {
            if (message.getMessage().equals("Post deleted ")) break;
            String postIdFlymer = "wpt" + message.getUserId().toString() + "_" + message.getVkId().toString();
            if (postIdFlymer.equals(postId)) {
                List<Media> mediaList = message.getMediaList();
                if (mediaList == null) mediaList = new ArrayList<>();
                mediaList.add(media);
                message.setMediaList(mediaList);
                break;
            }
            for (FlymerReply reply: message.getFlymerReplyList()) {
                String replyIdFlymer = "wpt" + message.getUserId().toString() + "_" + reply.getVkId().toString();
                if (replyIdFlymer.equals(postId)) {
                    List<Media> mediaList = reply.getMediaList();
                    if (mediaList == null) mediaList = new ArrayList<>();
                    mediaList.add(media);
                    reply.setMediaList(mediaList);
                    break loop;
                }
            }
        }
    }
}
