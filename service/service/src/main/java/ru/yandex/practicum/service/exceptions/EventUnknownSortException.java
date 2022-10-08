package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при передаче неподдерживаемого варианта сортировки
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventUnknownSortException extends ForbiddenException {
    public EventUnknownSortException() {
        super("Данный вариант сортировки не поддерживается");
    }
}