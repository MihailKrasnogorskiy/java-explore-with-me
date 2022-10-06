package ru.yandex.practicum.service.services.event;

import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;

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
    EventFullDto getEventByIdWhereUserIsOwner(long userId, long eventId);
}
