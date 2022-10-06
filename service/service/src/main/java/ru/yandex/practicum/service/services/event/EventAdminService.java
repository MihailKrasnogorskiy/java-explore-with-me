package ru.yandex.practicum.service.services.event;

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
    EventFullDto publishEvent(long eventId);

    /**
     * отклонение события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto rejectEvent(long eventId);
}
