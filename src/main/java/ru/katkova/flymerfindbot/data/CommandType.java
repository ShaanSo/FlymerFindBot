package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType implements UserAction{
    HELP("/help", "HELP_COMMAND_HANDLER"),
    MESSAGE("/message", "MESSAGE_COMMAND_HANDLER"),
    CLEAN("/clean", "CLEAN_COMMAND_HANDLER"),
    SHOW("/show", "SHOW_COMMAND_HANDLER"),
    SEND("/send", "SEND_COMMAND_HANDLER");

    public final String action;
    private final String handler;
}
