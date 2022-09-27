package ru.yandex.practicum.service.model.dto;

import java.util.List;

public class CompilationDto {
    private long id;
    private boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
