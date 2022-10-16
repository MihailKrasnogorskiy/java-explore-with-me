package ru.yandex.practicum.service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс сообщения обратной связи
 */

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@AllArgsConstructor
@Table(name = "feedback")
public class FeedbackPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "post")
    private String post;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "publish")
    private boolean isPublish;
}