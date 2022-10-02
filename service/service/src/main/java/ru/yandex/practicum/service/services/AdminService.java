package ru.yandex.practicum.service.services;

import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;

/**
 * интерфейс сервиса администраторов
 */
public interface AdminService {
    /**
     * создание пользователя
     *
     * @param dto - dto объект для создания пользователя
     * @return - полный dto объект пользователя
     */
    UserDto createUser(UserCreateDto dto);

    /**
     * удаление пользователя
     *
     * @param id - id пользователя
     */
    void deleteUser(long id);

    void validateUserId(long id);
}
