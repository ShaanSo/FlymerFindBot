package ru.katkova.flymerfindbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katkova.flymerfindbot.data.FlymerReply;
import java.util.List;

@Repository
public interface FlymerReplyRepository extends JpaRepository<FlymerReply, String> {

    List<FlymerReply> findAll();
}

