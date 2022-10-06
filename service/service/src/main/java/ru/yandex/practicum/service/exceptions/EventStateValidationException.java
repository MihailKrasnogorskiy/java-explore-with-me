package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации события менее чем за 2 часа до его начала
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventStateValidationException extends RuntimeException {
    public EventStateValidationException() {
        super("Опубликованное событие не может быть отклонено");
    }
}