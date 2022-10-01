package ru.yandex.practicum.statistic;

import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void create(EndpointHitDto dto);

    List<ViewStats> findStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, Boolean unique);
}
