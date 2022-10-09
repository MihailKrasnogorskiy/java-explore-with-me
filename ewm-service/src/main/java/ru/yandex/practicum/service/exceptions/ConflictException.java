package ru.yandex.practicum.service.exceptions;

/**
 * абстрактный класс исключений со статусом Conflict
 */
public abstract class ConflictException extends RuntimeException {
    public ConflictException(String s) {
        super(s);
    }
}
