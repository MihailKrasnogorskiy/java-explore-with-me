package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * класс dto подборок
 */
@Data
@Builder
public class CompilationDto {
    private long id;
    private boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
