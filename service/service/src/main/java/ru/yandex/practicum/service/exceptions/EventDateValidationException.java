package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации события менее чем за 2 часа до его начала
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventDateValidationException extends ForbiddenException {
    public EventDateValidationException(int lag) {
        super("Событие не может быть создано, изменено или опубликовано менее чем за " + lag + " час(а) до его начала");
    }
}