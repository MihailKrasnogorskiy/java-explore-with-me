package ru.yandex.practicum.service.services;

import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;

/**
 * интерфейс сервиса пользователей
 */
public interface UsersService {
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
    EventFullDto getEventByIdWhereUserIsOwner(long userId, long eventId);
}
