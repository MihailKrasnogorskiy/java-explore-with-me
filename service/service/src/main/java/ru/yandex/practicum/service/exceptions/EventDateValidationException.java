package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации события менее чем за 2 часа до его начала
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventDateValidationException extends RuntimeException {
    public EventDateValidationException() {
        super("Событие не может быть создано или изменено менее чем за два часа до его начала");
    }
}