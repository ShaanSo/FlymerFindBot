package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "TELEGRAM_USERS")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "chatid")
    private Long chatId;

    @Column
    private String userName;

    @Column
    private  Boolean isChannel;

    @Column
    private  Boolean isCommentsChat;

    @Column
    Mode mode;

    public User(Long chatId, String userName) {
        this.chatId = chatId;
        this.userName = userName;
    }
}
