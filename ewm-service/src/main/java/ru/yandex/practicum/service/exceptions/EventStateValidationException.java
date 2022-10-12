package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при проверке статуса события
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventStateValidationException extends ForbiddenException {
    /**
     * выбрасывается при изменении статуса события
     */
    public EventStateValidationException() {
        super("Опубликованное событие не может быть отклонено либо изменено");
    }

    public EventStateValidationException(String s) {
        super(s);
    }
}