package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

/**
 * класс dto обновления события пользователем
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateEventRequest {
    @Positive
    private long eventId;
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private String title;
}
