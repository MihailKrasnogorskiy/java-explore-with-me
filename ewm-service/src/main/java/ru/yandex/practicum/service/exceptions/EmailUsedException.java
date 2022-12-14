package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации пользователя с уже зарегистрированным email
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailUsedException extends ConflictException {
    public EmailUsedException(String email) {
        super(String.format("Пользователь с email '%s' уже зарегистрирован", email));
    }
}