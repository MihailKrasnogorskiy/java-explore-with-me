package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * класс короткого dto события
 */
@Data
@Builder
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private String eventDate;
    private long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
}
