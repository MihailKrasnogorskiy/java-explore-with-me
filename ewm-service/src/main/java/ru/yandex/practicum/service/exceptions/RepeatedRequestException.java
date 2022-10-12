package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке повторной подачи запроса на событие
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RepeatedRequestException extends ForbiddenException {
    public RepeatedRequestException() {
        super("Дублирование запросов запрещено.");
    }
}