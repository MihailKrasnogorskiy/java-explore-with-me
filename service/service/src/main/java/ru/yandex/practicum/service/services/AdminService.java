package ru.yandex.practicum.service.services;

import ru.yandex.practicum.service.model.dto.*;

import java.util.List;

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

    boolean validateUserId(long id);

    /**
     * запрос списка пользователей
     *
     * @param ids  - список id пользователей
     * @param from - количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size - количество элементов в наборе
     * @return - список пользователей
     */
    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    /**
     * создание категории
     *
     * @param dto - dto объект для создания категории
     * @return - dto объект категории
     */
    CategoryDto createCategory(NewCategoryDto dto);

    /**
     * обновление категории
     *
     * @param dto - dto объект для обновления категории
     * @return - dto объект категории
     */
    CategoryDto updateCategory(CategoryDto dto);

    /**
     * удаление категории
     *
     * @param id - id категории
     */
    void deleteCategory(long id);

    /**
     * публикация события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    EventFullDto publishEvent(long eventId);
}
