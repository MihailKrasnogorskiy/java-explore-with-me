package ru.yandex.practicum.statistic.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * маппер просмотра эндпоинта
 */
public class EndpointHitMapper {
    /**
     * маппинг просмотра эндпоинта из объекта dto  объект просмотра
     * @param dto - dto объект просмотра
     * @return - объект просмотра
     */
    public static EndpointHit toEndpointHit(EndpointHitDto dto) {
        EndpointHit hit = EndpointHit.builder()
                .app(dto.getApp())
                .ip(dto.getIp())
                .uri(dto.getUri())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        return hit;
    }
}
