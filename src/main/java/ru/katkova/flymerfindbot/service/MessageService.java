package ru.katkova.flymerfindbot.service;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import ru.katkova.flymerfindbot.data.Media;
import ru.katkova.flymerfindbot.data.MediaType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessageService {
    @Autowired
    private MediaService mediaService;

    @Value("${vk.mediaUrl}")
    private String mediaUrl;

    @Value("${tg.parseMode}")
    private String parseMode;

    @Value("${tg.messageLength}")
    private int messageLength;

    @Value("${tg.captionLength}")
    private int captionLength;


    @SneakyThrows
    public PartialBotApiMethod<Message> mapToTelegramMessage(ru.katkova.flymerfindbot.data.Message flymerMessage, Long chatId) {
        PartialBotApiMethod<Message> partialBotApiMethod;

        String textMessage = fillTextMessage(flymerMessage);

        //в сообщении одна картинка
        if (flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() == 1
                && flymerMessage.getMediaList().get(0).getType().equals(MediaType.IMAGE.getType())) {
            String caption = flymerMessage.trimToShow(textMessage, captionLength);
            partialBotApiMethod = SendPhoto.builder()
                    .photo(new InputFile(flymerMessage.getMediaList().get(0).getMediaUrl()))
                    .caption(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
        }
        //в сообщении один стикер
        else if (flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() == 1 && flymerMessage.getMediaList().get(0).getType().equals(MediaType.STICKER.getType())) {
            String caption = flymerMessage.trimToShow(textMessage, messageLength);
            caption = caption + String.format("<a href=\"%s\">sticker</a>",flymerMessage.getMediaList().get(0).getMediaUrl());
            partialBotApiMethod = SendMessage.builder()
                    .text(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
        }
        //в сообщении одна гифка
        else if (flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() == 1 && flymerMessage.getMediaList().get(0).getType().equals(MediaType.GIF.getType())) {
            String caption = flymerMessage.trimToShow(textMessage, messageLength);
            caption = caption + String.format("<a href=\"%s\">gif</a>",flymerMessage.getMediaList().get(0).getMediaUrl());
            partialBotApiMethod = SendMessage.builder()
                    .text(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
        }
        //в сообщении одна картинка + музыка
        else if (flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() > 1
                && flymerMessage.getMediaList().stream().filter(media -> media.getType().equals("image")).count() == 1) {
            String caption = textMessage;
            String url = "";
            for (Media media: flymerMessage.getMediaList()) {
                if (media.getType().equals("audio")) {
                    caption = caption + "\n\n" + "<i>" + media.getMediaUrl()+ "</i>";
                } else if (media.getType().equals("image")) {
                    url = flymerMessage.getMediaList().get(0).getMediaUrl();
                }
            }
            caption = flymerMessage.trimToShow(caption, captionLength);
            partialBotApiMethod = SendPhoto.builder()
                    .caption(caption)
                    .photo(new InputFile(url))
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
        }

        //в сообщении текст и музыка
        else if (flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() > 1
                && flymerMessage.getMediaList().stream().filter(media -> media.getType().equals("audio")).count() == flymerMessage.getMediaList().size() )
            {
            String caption = textMessage;
            for (Media media: flymerMessage.getMediaList()) {
                if (media.getType().equals("audio")) {
                    caption = caption + "\n\n" + "<i>" + media.getMediaUrl()+ "</i>";
                }
            }
            caption = flymerMessage.trimToShow(caption, messageLength);
            partialBotApiMethod = SendMessage.builder()
                    .text(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
            }
        //в сообщении одна гифка + музыка
        else if ((flymerMessage.getMediaList() != null && flymerMessage.getMediaList().size() > 1
                && flymerMessage.getMediaList().stream().filter(media -> media.getType().equals("gif")).count() == 1)) {
            String caption = textMessage;
            String url = "";
            for (Media media: flymerMessage.getMediaList()) {
                if (media.getType().equals("audio")) {
                    caption = caption + "\n\n" + "<i>" + media.getMediaUrl()+ "</i>";
                } if (media.getType().equals("gif")) {
                     url = media.getMediaUrl();
                }
            }
            caption = caption + String.format("<a href=\"%s\">gif</a>",url);
            caption = flymerMessage.trimToShow(caption, messageLength);
            partialBotApiMethod = SendMessage.builder()
                    .text(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
            return partialBotApiMethod;
        }
        //в сообщении только текст
        else {
            String caption = flymerMessage.trimToShow(textMessage, messageLength);
            partialBotApiMethod = SendMessage.builder()
                    .text(caption)
                    .chatId(chatId)
                    .parseMode(parseMode)
                    .build();
        }
        return partialBotApiMethod;
    }

    public PartialBotApiMethod<ArrayList<Message>> mapMediaListToTelegramMessage(ru.katkova.flymerfindbot.data.Message flymerMessage, Long chatId) {
        PartialBotApiMethod<ArrayList<Message>> partialBotApiMethod;

        String caption = "Автор: "+ flymerMessage.getUserLogin() + "\n" +
                "Дата: "+ fillDate(flymerMessage) + "\n" +
                "Сообщение: " + flymerMessage.getMessage();

        List<InputMedia> inputMediaList= new ArrayList<>();
        for (Media media: flymerMessage.getMediaList()) {
            if (media.getType().equals("image")) {
                InputMedia inputMedia = InputMediaPhoto.builder()
                        .media(media.getMediaUrl())
                        .build();
                inputMediaList.add(inputMedia);
            } else if (media.getType().equals("audio")) {
                caption = caption + "\n\n" + media.getMediaUrl();
            }
        }
        flymerMessage.trimToShow(caption, captionLength);
        inputMediaList.get(0).setCaption(caption);
        partialBotApiMethod = SendMediaGroup.builder()
                .medias(inputMediaList)
                .chatId(chatId)
                .build();
        return partialBotApiMethod;
    }

    private String escape(String message) {
        return message
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replaceAll("[i][d]\\d+[|]","");
//                .replaceAll("(?=_)","\\")
//                .replace("_", "\\_")
//                .replace("*", "\\*")
//                .replace("[", "\\[")
//                .replace("]", "\\]")
//                .replace("`", "\\`")
//                .replaceAll("<(.*?)\\>"," ");
    }

    @SneakyThrows
    public void fillMediaContent(List<FlymerMessage> messageList) {
        Document doc = Jsoup.connect(mediaUrl).get();
        Elements pagePostSizedThumbs = doc.select(".page_post_sized_thumbs");
        for (Element pagePostSizedThumb : pagePostSizedThumbs) {
            Elements elements = pagePostSizedThumb.children();
            List<Media> mediaList = new ArrayList<>();
            String postId = pagePostSizedThumb.parent().id();
            for (Element pageImage: elements) {
                String imageData = pageImage.toString();
                String search = "background-image: url(";
                int i = imageData.indexOf(search) + search.length();
                String substring = imageData.substring(i);
                String imageUrl = substring.substring(0, substring.indexOf(")"));
                imageUrl = imageUrl.replace("&amp;", "&");
                Media media = fillMediaItem(MediaType.IMAGE, imageUrl, postId);
                mediaList.add(media);
            }
            mediaService.addImagesToMessage(mediaList, messageList, postId);
        }

        Elements stickers = doc.select(".sticker_img");
        for (Element sticker : stickers) {
            String postId = sticker.parent().parent().id();
            if (postId.equals("")) {
                postId = sticker.parent().parent().parent().id();
                if (postId.equals("")) {
                    postId = sticker.parent().parent().parent().parent().id();
                }
            }
            String stickerData = sticker.toString();
            String search = "src=\"";
            int i = stickerData.indexOf(search) + search.length();
            String substring = stickerData.substring(i);
            String imageUrl = substring.substring(0, substring.indexOf("\""));
            imageUrl = imageUrl.replace("&amp;", "&");
            Media media = fillMediaItem(MediaType.STICKER, imageUrl, postId);
            mediaService.addMediaToMessage(media, messageList, postId);
        }

        Elements audioRow   = doc.select(".audio_row");
        for (Element audioRowItem : audioRow) {
            String postId = audioRowItem.parent().parent().id();
            Elements performerData = audioRowItem.select(".audio_row__performers");
            String performer = performerData.get(0).child(0).toString();
            String search = ">";
            int i = performer.indexOf(search) + search.length();
            String substringPerformer = performer.substring(i);
            String performerName = substringPerformer.substring(0, substringPerformer.indexOf("<"));
            Elements titleData = audioRowItem.select(".audio_row__title_inner");
            String title = titleData.get(0).toString();
            int j = title.indexOf(search) + search.length();
            String substringTitle = title.substring(j);
            String titleName = substringTitle.substring(0, substringTitle.indexOf("<"));
            String audioUrl = performerName + " - " + titleName;
            Media media = fillMediaItem(MediaType.AUDIO, audioUrl, postId);
            mediaService.addMediaToMessage(media, messageList, postId);
        }

        Elements gifElements = doc.select(".post_thumbed_media");
        for (Element gifItem : gifElements) {
            String postId = gifItem.parent().id();
            String gifData = gifItem.child(0).child(0).toString();
            String search = "href=\"";
            int i = gifData.indexOf(search) + search.length();
            String substring = gifData.substring(i);
            String imageUrl = "https://vk.com" + substring.substring(0, substring.indexOf("\""));
            imageUrl = imageUrl.replace("&amp;", "&");
            Media media = fillMediaItem(MediaType.GIF, imageUrl, postId);
            mediaService.addMediaToMessage(media, messageList, postId);
        }
    }

    private Media fillMediaItem(MediaType mediaType, String mediaItemUrl, String postId) {
        Media media = new Media();
        media.setMediaUrl(mediaItemUrl);
        media.setType(mediaType.getType());
        media.setVkPostId(postId);
        return media;
    }

    private String fillTextMessage(ru.katkova.flymerfindbot.data.Message flymerMessage) {
        String textMessage = "<b>Автор: </b> "+ flymerMessage.getUserLogin() + "\n" +
                "<b>Дата: </b> "+ fillDate(flymerMessage) + "\n" +
                "<b>Сообщение: </b> " + escape(flymerMessage.getMessage());
        return textMessage;
    }

    private String fillDate(ru.katkova.flymerfindbot.data.Message flymerMessage) {
        Date date = new Date((long) flymerMessage.getDate() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return df.format(date);
    }
}
