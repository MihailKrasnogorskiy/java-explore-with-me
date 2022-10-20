package ru.yandex.practicum.service.controllers.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;
import ru.yandex.practicum.service.services.compilation.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * класс контроллера подборок для администраторов
 */
@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
public class CompilationAdminController {

    private final CompilationAdminService service;

    @Autowired
    public CompilationAdminController(CompilationAdminService service) {
        this.service = service;
    }

    /**
     * создание подборки
     *
     * @param dto - dto объект для создания подборки
     * @return - dto объект подборки
     */
    @PostMapping()
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        return service.create(dto);
    }

    /**
     * удаление подборки
     *
     * @param compId - id подборки
     */
    @DeleteMapping("{compId}")
    public void delete(@Positive @PathVariable long compId) {
        service.delete(compId);
    }

    /**
     * открепление подборки
     *
     * @param compId - id подборки
     */
    @DeleteMapping("{compId}/pin")
    public void deletePin(@Positive @PathVariable long compId) {
        service.deletePin(compId);
    }

    /**
     * закрепление подборки
     *
     * @param compId - id подборки
     */
    @PatchMapping("{compId}/pin")
    public void pinned(@Positive @PathVariable long compId) {
        service.pinned(compId);
    }

    /**
     * удаление события из подборки
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    @DeleteMapping("{compId}/events/{eventId}")
    public void deleteEvent(@Positive @PathVariable long compId, @Positive @PathVariable long eventId) {
        service.deleteEvent(compId, eventId);
    }

    /**
     * добавление события в подборку
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    @PatchMapping("{compId}/events/{eventId}")
    public void addEvent(@Positive @PathVariable long compId, @Positive @PathVariable long eventId) {
        service.addEvent(compId, eventId);
    }
}
