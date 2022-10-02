package ru.yandex.practicum.service.model.dto;

import lombok.Data;

/**
 * класс короткого dto пользователя
 */
@Data
public class UserShortDto {
    private long id;
    private String name;
}
