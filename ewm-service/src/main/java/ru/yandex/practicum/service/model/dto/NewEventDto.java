package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewEventDto {
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
}
