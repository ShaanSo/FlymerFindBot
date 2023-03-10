package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {

    CREATE("/create"),
    SET_TEXT("/text"),
    SET_IMAGE("/image"),
    SEND("send");

    public String command;
}
