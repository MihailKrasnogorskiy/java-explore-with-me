package ru.yandex.practicum.statistic;

import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * интерфейс сервиса статистики
 */
public interface StatService {
    /**
     * сохранение просмотра
     *
     * @param dto - dto объект просмотра эндпонта
     */
    void create(EndpointHitDto dto);

    /**
     * запрос статистики
     *
     * @param startTime - дата и время начала диапазона за который нужно выгрузить статистик
     * @param endTime   - дата и время конца диапазона за который нужно выгрузить статистику
     * @param unique    - нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @param uris      - список uri для которых нужно выгрузить статистику
     * @return - список объектов возвращаемой статистики
     */
    List<ViewStats> findStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, Boolean unique);
}
