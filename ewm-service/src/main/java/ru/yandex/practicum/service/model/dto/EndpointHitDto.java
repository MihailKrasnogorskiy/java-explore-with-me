package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto класс просмотра эндпоинта
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}