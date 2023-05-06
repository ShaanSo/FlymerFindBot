package ru.katkova.flymerfindbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.katkova.flymerfindbot.data.Mode;
import ru.katkova.flymerfindbot.data.User;
import ru.katkova.flymerfindbot.repository.UserRepository;
import javax.transaction.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createNewUser(Long chatId, String userName, Boolean isChannel) {
        log.info("User with chat id " + chatId + " was created in DB");
        User user = new User(chatId, userName, isChannel, Mode.NONE);
        userRepository.save(user);
    }

    public boolean existsInDB(Long chatId) {
        return !(userRepository.findFirstByChatId(chatId) == null);
    }
    public User findInDB(Long chatId) {
        return userRepository.findFirstByChatId(chatId);
    }


    @Transactional
    public void deleteUser(Long chatId) {
        log.info("User with chat id " + chatId + " was deleted from DB");
        userRepository.deleteAllByChatId(chatId);
    }

    public void changeMode(User user, Mode mode) {
        user.setMode(mode);
        userRepository.save(user);
    }
}
