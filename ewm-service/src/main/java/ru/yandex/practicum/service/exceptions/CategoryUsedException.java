package ru.yandex.practicum.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * исключение выбрасываемое при попытке регистрации пользователя с уже зарегистрированным email
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryUsedException extends ConflictException {
    public CategoryUsedException(long id) {
        super(String.format("Категория с id = '%s' ещё используется", id) );
    }
}