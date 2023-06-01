package ru.katkova.flymerfindbot.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @Column
    @GeneratedValue()
    private Integer id;

    @Column
    private Integer vkId;

    @Column
    private Integer telegramId;

    @Column
    private String source;

    @Column
    private Integer date;

    @Column(columnDefinition="text")
    private String message;

    @Column
    private String userLogin;

    @Column
    private String additionalIds;

    @Column
    private Integer userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id")
    private List<Media> mediaList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id")
    private List<FlymerReply> flymerReplyList;

    private boolean updated;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Message)) {
            return false;
        } else {
            Message flymerMessage = (Message) o;
            return Objects.equals(this.vkId, flymerMessage.getVkId()) && Objects.equals(this.userId, flymerMessage.getUserId());
        }
    }

    public String trimToShow(String textMessage, int trimLength) {
        return textMessage;
    }
}
