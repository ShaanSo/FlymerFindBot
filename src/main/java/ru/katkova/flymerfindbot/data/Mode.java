package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {
    CREATE("create"),
    NONE("none"),
    SET_TEXT("text"),
    SET_IMAGE("image"),
    SET_MESSAGE("message"),
    SEND("send");

    private final String value;
}
