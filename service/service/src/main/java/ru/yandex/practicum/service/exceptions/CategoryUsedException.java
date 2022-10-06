package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации пользователя с уже зарегистрированным email
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryUsedException extends RuntimeException {
    public CategoryUsedException(long id) {
        super("Категория с id = " + id + " ещё используется");
    }
}