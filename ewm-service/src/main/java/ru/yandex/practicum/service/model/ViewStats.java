package ru.yandex.practicum.service.model;

import lombok.Builder;
import lombok.Data;

/**
 * класс возвращаемой статистики
 */
@Data
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}