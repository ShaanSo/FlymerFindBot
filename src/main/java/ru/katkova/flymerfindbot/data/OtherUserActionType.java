package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OtherUserActionType implements UserAction {

    MESSAGE("message", "RAW_MESSAGE_HANDLER"),
    HELP("help", "HELP_CALLBACK_HANDLER");

    public final String action;
    private final String handler;
}
