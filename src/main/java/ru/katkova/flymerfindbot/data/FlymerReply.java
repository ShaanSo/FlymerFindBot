package ru.katkova.flymerfindbot.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FLYMER_REPLY")
public class FlymerReply extends Message{

    @ManyToOne
    @JoinColumn(name = "comments_id")
    private FlymerMessage flymerMessage;

    @Column
    private String vkPostId;

    @Override
    public String trimToShow(String textMessage, int trimLength) {
        if (textMessage.length() > trimLength) {
            String vkUrl = String.format(seeMore, this.getVkPostId());
            vkUrl = vkUrl.replace("_", "\\_");
            int length = vkUrl.length();
            return textMessage.substring(0,trimLength - length) + vkUrl;
        } else {
            return textMessage;
        }
    }
}
