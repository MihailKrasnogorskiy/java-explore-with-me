package ru.yandex.practicum.service.services.event;

import ru.yandex.practicum.service.model.dto.*;

import java.util.List;

/**
 * интерфейс сервиса событий для пользователей
 */
public interface EventUsersService {
    /**
     * создание события
     *
     * @param userId - id пользователя
     * @param dto    - dto объект создаваемого события
     * @return - dto объект созданного события
     */
    EventFullDto createEvent(long userId, NewEventDto dto);

    /**
     * запрос события созданного пользователем по id
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto findEventByIdWhereUserIsOwner(long userId, long eventId);

    /**
     * получение событий, добавленных текущим пользователем
     *
     * @param userId - id ользователя
     * @param from   - количество подборок, которые нужно пропустить для формирования текущего набора
     * @param size   - количество подборок в наборе
     * @return - список коротких dto событий
     */
    List<EventShortDto> findAllWhereUserIsOwner(long userId, Integer from, Integer size);

    /**
     * отмена события пользователем
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto cancelEvent(long userId, long eventId);

    /**
     * обновление события пользователем
     *
     * @param userId - id пользователя
     * @param dto    - dto объект с данными для обновления
     * @return - полный dto объект события
     */
    EventFullDto update(long userId, UpdateEventRequest dto);

    /**
     * получение списка всех запросов на участие в событии пользователя
     *
     * @param userId - id пользователя
     * @return - список запросов
     */
    List<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId);

    /**
     * подтверждение запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param reqId   - id запроса
     * @param eventId - id события
     * @return - dto объект запроса
     */
    ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId);

    /**
     * отклонение запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param reqId   - id запроса
     * @param eventId - id события
     * @return - dto объект запроса
     */
    ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId);
}
