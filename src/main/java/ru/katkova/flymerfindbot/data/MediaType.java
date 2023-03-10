package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaType {
    IMAGE("image"),

    VIDEO("video"),

    AUDIO("audio"),

    GIF("gif"),
    STICKER("sticker");

    private final String type;
}
