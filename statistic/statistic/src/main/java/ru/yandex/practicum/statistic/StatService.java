package ru.yandex.practicum.statistic;

import ru.yandex.practicum.statistic.model.EndpointHitDto;

public interface StatService {
    void create(EndpointHitDto dto);
}
