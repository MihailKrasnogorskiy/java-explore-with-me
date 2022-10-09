package ru.yandex.practicum.statistic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс просмотра эндпоинта
 */
@Entity(name = "hits")
@Data
@Builder
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

    public EndpointHit() {

    }
}
