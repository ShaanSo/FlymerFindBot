package ru.katkova.flymerfindbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.katkova.flymerfindbot.data.RawMessage;
import ru.katkova.flymerfindbot.repository.RawMessageRepository;

@Service
public class RawMessageService {

    @Autowired
    private RawMessageRepository rawMessageRepository;

    public RawMessage findByChatId(Long chatId){
        return rawMessageRepository.findFirstByChatId(chatId);
    }

    public void delete(RawMessage rawMessage) {
        rawMessageRepository.delete(rawMessage);
    }

    public void save(RawMessage rawMessage) {
        rawMessageRepository.save(rawMessage);
    }
}
