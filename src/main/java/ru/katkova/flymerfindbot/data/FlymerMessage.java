package ru.katkova.flymerfindbot.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "FLYMER_MESSAGE")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FlymerMessage extends Message {

    @Column
    Integer commentsId;

    @Override
    public String trimToShow(String textMessage, int trimLength) {
        if (textMessage.length() > trimLength) {
            String vkUrl = String.format(seeMore, this.getUserId(), this.getVkId());
            int length = vkUrl.length();
            return textMessage.substring(0,trimLength - length) + vkUrl;
        } else {
            return textMessage;
        }
    }
}
