package ru.katkova.flymerfindbot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.katkova.flymerfindbot.data.FlymerReply;
import ru.katkova.flymerfindbot.repository.FlymerReplyRepository;
import java.util.List;

@Service
public class FlymerReplyService extends MessageService {

    @Autowired
    private FlymerReplyRepository flymerReplyRepository;

    public List<FlymerReply> findAll() {
        return flymerReplyRepository.findAll();
    }

    public void save(FlymerReply flymerReply) {
        flymerReplyRepository.save(flymerReply);
    }

    public FlymerReply findMessage(Integer vkId, Integer userId) {
        return flymerReplyRepository.findFirstByVkIdAndUserId(vkId, userId);
    }

}
