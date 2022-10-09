package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * класс короткого dto пользователя
 */
@Data
@Builder
public class UserShortDto {
    private long id;
    private String name;
}
