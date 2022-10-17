package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке изменения состояния события не его инициатором
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventOwnerValidationException extends ForbiddenException {
    public EventOwnerValidationException(long userId, long eventId, String s) {
        super(String.format("Пользователь с id = '%s' '%s'инициатором события с id = '%s'", userId, s, eventId));
    }
}