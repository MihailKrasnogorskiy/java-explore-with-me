package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке отмены запроса на участие в событии не заявителем
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RequestOwnerValidationException extends ForbiddenException {
    public RequestOwnerValidationException(long userId, long requestId) {
        super(String.format("Пользователь с id = '%s' не создавал запрос с id = '%s'", userId, requestId));
    }
}