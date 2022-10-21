package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.service.model.EventState;

/**
 * класс полного dto объекта события
 */
@Data
@Builder
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private String createdOn;
    private String description;
    private String eventDate;
    private long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
    private int participantLimit;
    private boolean requestModeration;
    private String publishedOn;
    private EventState state;
    private long confirmedRequests;
    private Location location;
}
