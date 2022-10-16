package ru.yandex.practicum.service.services.event;

import ru.yandex.practicum.service.model.SortMethod;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;

import java.util.List;

/**
 * интерфейс сервиса публичного контроллера событий
 */
public interface EventPublicService {
    /**
     * запрос событий с возможностью фильтрации
     *
     * @param text          - текст для поиска в содержимом аннотации и подробном описании события
     * @param categories    - список идентификаторов категорий в которых будет вестись поиск
     * @param paid          - поиск только платных/бесплатных событий
     * @param rangeStart    - дата и время не раньше которых должно произойти событие
     * @param rangeEnd      - дата и время не позже которых должно произойти событие
     * @param onlyAvailable - только события у которых не исчерпан лимит запросов на участие
     * @param sortMethod    - вариант сортировки
     * @param from          - количество событий, которые нужно пропустить для формирования текущего набора
     * @param size          - количество событий в наборе
     * @return - список событий
     */
    List<EventFullDto> findAll(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                               Boolean onlyAvailable, SortMethod sortMethod, Integer from, Integer size);

    /**
     * запрос опубликованного события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto findById(long eventId);

}
