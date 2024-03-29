package ru.katkova.flymerfindbot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.katkova.flymerfindbot.data.User;
import ru.katkova.flymerfindbot.data.UserAction;
import java.util.List;

public interface UserActionHandler {
    List<PartialBotApiMethod<?>> handle(User user, Update update);
    UserAction getAction();
}
