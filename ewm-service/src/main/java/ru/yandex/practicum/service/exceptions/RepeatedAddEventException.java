package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке повторной подачи запроса на событие
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RepeatedAddEventException extends ForbiddenException {
    public RepeatedAddEventException() {
        super("Повторное добавление событий запрещено");
    }
}