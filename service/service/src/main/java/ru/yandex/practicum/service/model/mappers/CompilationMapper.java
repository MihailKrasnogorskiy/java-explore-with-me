package ru.yandex.practicum.service.model.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.model.Compilation;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;
import ru.yandex.practicum.service.repositoryes.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Component
public class CompilationMapper {
    private final EventRepository eventRepository;
    @Lazy
    private EventMapper eventMapper;

    @Autowired
    public CompilationMapper(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * создание объекта подборки из dto объекта для создания подборки
     *
     * @param dto - dto объект для создания подборки
     * @return - объект подборки
     */
    public Compilation toCompilation(NewCompilationDto dto) {
        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned() != null && dto.getPinned())
                .events(new HashSet<>())
                .build();
        dto.getEvents().stream()
                .map(eventRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(compilation.getEvents()::add);
        return compilation;
    }

    /**
     * создание dto объекта подборки из объекта подборки
     *
     * @param compilation - dto объект для создания подборки
     * @return - dto объект подборки
     */
    public CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(new ArrayList<>())
                .build();
        compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .forEach(dto.getEvents()::add);
        return dto;
    }
}
