package ru.katkova.flymerfindbot;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.katkova.flymerfindbot.data.User;
import ru.katkova.flymerfindbot.service.FlymerReplyService;
import ru.katkova.flymerfindbot.service.MediaService;

@ComponentScan
@SpringBootConfiguration
@Configuration
public class ApplicationConfiguration {


    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public HttpTransportClient httpTransportClient() {
        return new HttpTransportClient();
    }

    @Bean
    public VkApiClient vkApiClient(HttpTransportClient httpTransportClient) {return new VkApiClient(httpTransportClient);}

    @Bean
    public FlymerReplyService flymerReplyService() {
        return new FlymerReplyService();
    }

    @Bean
    public MediaService mediaService() {
        return new MediaService();
    }
}
