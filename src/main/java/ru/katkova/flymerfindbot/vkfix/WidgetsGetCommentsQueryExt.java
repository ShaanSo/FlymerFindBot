package ru.katkova.flymerfindbot.vkfix;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.users.Fields;
import java.util.Arrays;
import java.util.List;

public class WidgetsGetCommentsQueryExt extends AbstractQueryBuilder<WidgetsGetCommentsQueryExt, GetCommentsResponseExt> {
    public WidgetsGetCommentsQueryExt(VkApiClient client, UserActor actor) {
        super(client, "widgets.getComments", GetCommentsResponseExt.class);
        this.accessToken(actor.getAccessToken());
    }

    public WidgetsGetCommentsQueryExt(VkApiClient client, ServiceActor actor) {
        super(client, "widgets.getComments", GetCommentsResponseExt.class);
        this.accessToken(actor.getAccessToken());
        this.clientSecret(actor.getClientSecret());
    }

    public WidgetsGetCommentsQueryExt widgetApiId(Integer value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("widget_api_id", value);
    }

    public WidgetsGetCommentsQueryExt url(String value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("url", value);
    }

    public WidgetsGetCommentsQueryExt pageId(String value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("page_id", value);
    }

    public WidgetsGetCommentsQueryExt order(String value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("order", value);
    }

    public WidgetsGetCommentsQueryExt offset(Integer value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("offset", value);
    }

    public WidgetsGetCommentsQueryExt count(Integer value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("count", value);
    }

    public WidgetsGetCommentsQueryExt fields(Fields... value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("fields", value);
    }

    public WidgetsGetCommentsQueryExt fields(List<Fields> value) {
        return (WidgetsGetCommentsQueryExt)this.unsafeParam("fields", value);
    }

    protected WidgetsGetCommentsQueryExt getThis() {
        return this;
    }

    protected List<String> essentialKeys() {
        return Arrays.asList("access_token");
    }
}
