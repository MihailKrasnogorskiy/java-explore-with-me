package ru.yandex.practicum.statistic.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "hits")
@Data
@Builder
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    @JoinColumn(name = "views_date")
    private LocalDateTime timestamp;

    public EndpointHit() {

    }
}
