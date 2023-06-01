package ru.katkova.flymerfindbot.data;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "MEDIA")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Media {

    @Id()
    @Column
    @GeneratedValue()
    private Integer id;

    @Column(columnDefinition="text")
    private String mediaUrl;

    @Column
    private String type;

    @Column
    private String vkPostId;

    @ManyToOne
    private Message message;
}
