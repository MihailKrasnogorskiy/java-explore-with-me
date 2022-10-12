package ru.yandex.practicum.service.exceptions;

/**
 * абстрактный класс исключений со статусом Forbidden
 */
public abstract class ForbiddenException extends RuntimeException {
    public ForbiddenException(String s) {
        super(s);
    }
}
