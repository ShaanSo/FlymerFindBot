package ru.katkova.flymerfindbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.katkova.flymerfindbot.data.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, String> {

}
