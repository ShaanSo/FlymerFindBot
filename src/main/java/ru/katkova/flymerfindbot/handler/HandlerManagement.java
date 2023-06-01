package ru.katkova.flymerfindbot.handler;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toMap;

@Component
public class HandlerManagement {

    private Map<UserAction, UserActionHandler> handlers;
    private final UserActionHandler defaultHandler;

    public HandlerManagement (Collection<UserActionHandler> handlers, UserActionHandler defaultHandler){
        this.handlers = handlers.stream().collect(toMap(UserActionHandler::getAction, Function.identity()));
        this.defaultHandler = defaultHandler;
    }

    public List<PartialBotApiMethod<?>> manage(User user, Update update) {
        return handlers.getOrDefault(getUserAction(update, user), defaultHandler).handle(user, update);
    }

    @SneakyThrows
    public static UserAction getUserAction(Update update, User user) {
        UserAction userAction;
        if (update.hasMessage() && update.getMessage().isCommand()) {
            userAction = EnumSet.allOf(CommandType.class)
                    .stream()
                    .filter(command -> command.getAction().equalsIgnoreCase(update.getMessage().getText()))
                    .findFirst().orElse(CommandType.HELP);
        }
        else if (update.hasMessage() && (update.getMessage().hasPhoto() || update.getMessage().hasText())) {
            if (user.getMode() != null && user.getMode().equals(Mode.SET_MESSAGE)) return OtherUserActionType.MESSAGE;
            else return CommandType.HELP;
        }
        else return CommandType.HELP;
        return userAction;
    }
}
