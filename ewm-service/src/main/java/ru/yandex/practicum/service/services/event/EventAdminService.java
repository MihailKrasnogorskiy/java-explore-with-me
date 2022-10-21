package ru.yandex.practicum.service.services.event;

import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.service.model.dto.EventFullDto;

import java.util.List;

/**
 * интерфейс сервиса событий для администраторов
 */
public interface EventAdminService {
    /**
     * публикация события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto publish(long eventId);

    /**
     * отклонение события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto reject(long eventId);

    /**
     * обновление события администратором
     *
     * @param eventId - id события
     * @param dto     - dto объект с данными для обновления
     * @return - полный dto объект события
     */
    EventFullDto update(long eventId, AdminUpdateEventRequest dto);

    /**
     * запрос событий с возможностью фильтрации
     *
     * @param users      - список id пользователей, чьи события нужно найти
     * @param states     - список состояний в которых находятся искомые события
     * @param categories - список идентификаторов категорий в которых будет вестись поиск
     * @param rangeStart - дата и время не раньше которых должно произойти событие
     * @param rangeEnd   - дата и время не позже которых должно произойти событие
     * @param from       - количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       - количество событий в наборе
     * @return - список событий
     */
    List<EventFullDto> findAll(List<Long> users, List<EventState> states, List<Long> categories, String rangeStart,
                               String rangeEnd, Integer from, Integer size);

}
