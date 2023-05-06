package ru.katkova.flymerfindbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katkova.flymerfindbot.data.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findFirstByChatId(Long chatId);
    void deleteAllByChatId(Long chatId);

}
