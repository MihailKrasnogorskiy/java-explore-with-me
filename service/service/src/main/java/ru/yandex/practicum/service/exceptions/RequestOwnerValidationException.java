package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке отмены заявки на участие в событии не заявителем
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RequestOwnerValidationException extends ForbiddenException {
    public RequestOwnerValidationException(long userId, long requestId) {
        super("Пользователь с id = " + userId + " не создавал заявку с id = " + requestId);
    }
}