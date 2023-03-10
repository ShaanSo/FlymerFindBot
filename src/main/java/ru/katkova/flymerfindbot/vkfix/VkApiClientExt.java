package ru.katkova.flymerfindbot.vkfix;

import com.google.gson.Gson;
import com.vk.api.sdk.actions.Widgets;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ext")
public class VkApiClientExt extends VkApiClient {

    @Autowired
    public VkApiClientExt(TransportClient transportClient) {
        super(transportClient);
    }

    public VkApiClientExt(TransportClient transportClient, Gson gson, int retryAttemptsInternalServerErrorCount) {
        super(transportClient, gson, retryAttemptsInternalServerErrorCount);
    }

//    public VkApiClientExt(TransportClient transportClient) {
//        super(transportClient);
//    }
//
//    public VkApiClientExt(TransportClient transportClient, Gson gson, int retryAttemptsInternalServerErrorCount) {
//        super(transportClient, gson, retryAttemptsInternalServerErrorCount);
//    }

    public WidgetsExt widgetsExt() {
        return new WidgetsExt(this);
    }

//    public TransportClient getTransportClient() {
//        return super.getTransportClient();
//    }


}
