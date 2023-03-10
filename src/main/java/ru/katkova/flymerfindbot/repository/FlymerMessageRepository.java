package ru.katkova.flymerfindbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katkova.flymerfindbot.data.FlymerMessage;
import java.util.List;

@Repository
public interface FlymerMessageRepository extends JpaRepository<FlymerMessage, String> {

    List<FlymerMessage> findAll();
    FlymerMessage findFirstByOrderByDate();

    FlymerMessage findFirstByTelegramId(Integer telegramId);


}
