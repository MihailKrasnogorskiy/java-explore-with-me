package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * класс полного dto объекта пользователя
 */
@Data
@Builder
public class UserDto {
    private long id;
    private String email;
    private String name;
}
