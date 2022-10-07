package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке изменения состояния события не его инициатором
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventOwnerValidationException extends ForbiddenException {
    public EventOwnerValidationException(long userId, long eventId, String s) {
        super("Пользователь с id = " + userId + s + "инициатором события с id =" + eventId);
    }
}