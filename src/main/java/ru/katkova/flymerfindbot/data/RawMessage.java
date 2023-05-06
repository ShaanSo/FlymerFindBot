package ru.katkova.flymerfindbot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "RAW_MESSAGE")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RawMessage extends Message {

    @Column
    private Long chatId;

}
