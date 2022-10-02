package ru.yandex.practicum.service.model.mappers;

import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;

/**
 * маппер пользователей
 */
public class UserMapper {
    /**
     * преобразование в объект пользователя
     *
     * @param dto - dto объект для создания пользователя
     * @return - полный dto объект пользователя
     */
    public static User toUser(UserCreateDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    /**
     * преобразование в полный dto объект пользователя
     *
     * @param user - объект пользователя
     * @return - полный dto объект пользователя
     */
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
