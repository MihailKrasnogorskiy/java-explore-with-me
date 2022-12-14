package ru.yandex.practicum.service.services.user;

import ru.yandex.practicum.service.model.dto.NewUserRequest;
import ru.yandex.practicum.service.model.dto.UserDto;

import java.util.List;

/**
 * интерфейс сервиса пользователей для администраторов
 */
public interface UserAdminService {
    /**
     * создание пользователя
     *
     * @param dto - dto объект для создания пользователя
     * @return - полный dto объект пользователя
     */
    UserDto createUser(NewUserRequest dto);

    /**
     * удаление пользователя
     *
     * @param id - id пользователя
     */
    void deleteUser(long id);

    /**
     * запрос списка пользователей
     *
     * @param ids  - список id пользователей
     * @param from - количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size - количество элементов в наборе
     * @return - список пользователей
     */
    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);
}
