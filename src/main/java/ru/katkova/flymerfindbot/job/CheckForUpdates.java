package ru.katkova.flymerfindbot.job;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.katkova.flymerfindbot.bot.Bot;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import ru.katkova.flymerfindbot.service.FlymerMessageService;
import ru.katkova.flymerfindbot.vkfix.GetCommentsResponseExt;
import ru.katkova.flymerfindbot.vkfix.VkApiClientExt;
import ru.katkova.flymerfindbot.vkfix.WidgetCommentExt;
import java.util.*;

@EnableScheduling
@Service
public class CheckForUpdates {

    @Autowired
    VkApiClientExt vkApiClientExt;

    @Autowired
    VkApiClient vkApiClient;

    @Autowired
    FlymerMessageService flymerMessageService;

    @Autowired
    private Bot bot;

    @Value("${vk.accessToken}")
    private String accessToken;

    @Value("${vk.appId}")
    private Integer appId;

    @Value("${vk.widgetApiId}")
    private Integer widgetApiId;

    @Value("${vk.pageId}")
    private String pageId;

    @Value("${vk.postCount}")
    private Integer postCount;

    @Value("${vk.unsafeParam}")
    private String unsafeParam;

    @SneakyThrows
    @Scheduled(fixedRateString = "${bot.fixedRate}")
    public List<FlymerMessage> check() {

        ServiceActor serviceActor = new ServiceActor(appId, accessToken);
        GetCommentsResponseExt getCommentsResponse = vkApiClientExt
                .widgetsExt()
                .getComments(serviceActor)
                .widgetApiId(widgetApiId)
                .pageId(pageId)
                .count(postCount)
                .unsafeParam("fields", "replies","user")
                .execute();

        List<WidgetCommentExt> widgetCommentList = getCommentsResponse.getPosts();
        widgetCommentList.sort(Comparator.comparingInt(WidgetCommentExt::getDate));
        List<FlymerMessage> flymerMessageList = new ArrayList<>();

        for (WidgetCommentExt widgetMessage : widgetCommentList) {
            FlymerMessage flymerMessage = flymerMessageService.mapFromWidget(widgetMessage);
            flymerMessageList.add(flymerMessage);
        }

        bot.perform(flymerMessageList, true);
        return flymerMessageList;
    }
}
