package ru.yandex.practicum.service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * класс dto обновления события администратором
 */
@Data
@NoArgsConstructor
public class AdminUpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
