package ru.yandex.practicum.statistic.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс просмотра эндпоинта
 */
@Entity(name = "hits")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    @Column(name = "views_date")
    private LocalDateTime timestamp;

}
