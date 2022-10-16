package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.service.model.EventState;

/**
 * класс полного dto объекта события
 */
@Data
@Builder
public class EventRevisionDto {
    private String annotation;
    private CategoryDto category;
    private String createdOn;
    private String description;
    private String eventDate;
    private long id;
    private boolean paid;
    private String title;
    private int participantLimit;
    private boolean requestModeration;
    private EventState state;
    private String adminComment;
}
