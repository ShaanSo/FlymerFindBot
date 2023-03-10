package ru.katkova.flymerfindbot.vkfix;

import com.vk.api.sdk.actions.Widgets;
import com.vk.api.sdk.client.AbstractAction;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.widgets.WidgetsGetCommentsQuery;

public class WidgetsExt extends AbstractAction {

    public WidgetsExt(VkApiClient client) {
        super(client);
    }

    public WidgetsGetCommentsQueryExt getComments(UserActor actor) {
        return new WidgetsGetCommentsQueryExt(this.getClient(), actor);
    }

    public WidgetsGetCommentsQueryExt getComments(ServiceActor actor) {
        return new WidgetsGetCommentsQueryExt(this.getClient(), actor);
    }
}
