package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * класс dto для создания нового события
 */
@Data
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @NotBlank
    private String annotation;
    @Min(value = 0)
    private long category;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotBlank
    private String eventDate;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @NotNull
    @NotBlank
    private String title;
}
