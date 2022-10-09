package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке подачи запроса на участие в событии, где набран лимит участников
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventIsNotAvailableException extends ForbiddenException {
    public EventIsNotAvailableException(long eventId) {
        super("На событие с id = " + eventId + " достигнут лимит участников");
    }
}