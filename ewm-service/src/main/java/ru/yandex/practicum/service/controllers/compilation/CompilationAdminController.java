package ru.yandex.practicum.service.controllers.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;
import ru.yandex.practicum.service.services.compilation.CompilationAdminService;

import javax.validation.constraints.Positive;

/**
 * класс контроллера подборок для администраторов
 */
@RestController
@RequestMapping(path = "/admin/compilations")
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
    public CompilationDto create(@RequestBody NewCompilationDto dto) {
        return service.create(dto);
    }

    /**
     * удаление подборки
     *
     * @param compId - id подборки
     */
    @DeleteMapping("{compId}")
    public void delete(@PathVariable @Positive long compId) {
        service.delete(compId);
    }

    /**
     * открепление подборки
     *
     * @param compId - id подборки
     */
    @DeleteMapping("{compId}/pin")
    public void deletePin(@PathVariable @Positive long compId) {
        service.deletePin(compId);
    }

    /**
     * закрепление подборки
     *
     * @param compId - id подборки
     */
    @PatchMapping("{compId}/pin")
    public void pinned(@PathVariable @Positive long compId) {
        service.pinned(compId);
    }

    /**
     * удаление события из подборки
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    @DeleteMapping("{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable @Positive long compId, @PathVariable @Positive long eventId) {
        service.deleteEvent(compId, eventId);
    }

    /**
     * добавление события в подборку
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    @PatchMapping("{compId}/events/{eventId}")
    public void addEvent(@PathVariable @Positive long compId, @PathVariable @Positive long eventId) {
        service.addEvent(compId, eventId);
    }
}
