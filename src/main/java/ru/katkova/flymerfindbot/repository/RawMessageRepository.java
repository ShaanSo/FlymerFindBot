package ru.katkova.flymerfindbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katkova.flymerfindbot.data.RawMessage;

@Repository
public interface RawMessageRepository extends JpaRepository<RawMessage, String> {

    RawMessage findFirstByChatId(Long chatId);


}
