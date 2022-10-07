package ru.yandex.practicum.service.services.event;

import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.service.model.dto.EventFullDto;

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
}
