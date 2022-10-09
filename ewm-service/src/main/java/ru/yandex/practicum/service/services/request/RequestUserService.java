package ru.yandex.practicum.service.services.request;

import ru.yandex.practicum.service.model.dto.ParticipationRequestDto;

import java.util.List;

/**
 * интерфейс сервиса запросов на участие в событиях для пользователей
 */
public interface RequestUserService {
    /**
     * создание запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - dto объект запроса на участие
     */
    ParticipationRequestDto create(long userId, long eventId);

    /**
     * отмена запроса на участие в событии
     *
     * @param userId    - id пользователя
     * @param requestId - id запроса
     * @return - dto объект запроса на участие
     */
    ParticipationRequestDto cancel(long userId, long requestId);

    /**
     * получение списка всех запросов пользователя на участие в чужих событиях
     *
     * @param userId - id пользователя
     * @return - список запросов
     */
    List<ParticipationRequestDto> findAllByRequesterId(long userId);
}
