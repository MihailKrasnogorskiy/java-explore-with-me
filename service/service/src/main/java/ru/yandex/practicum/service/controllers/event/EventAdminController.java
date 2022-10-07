package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.services.event.EventAdminService;

import javax.validation.constraints.Positive;

/**
 * контроллер событий для администраторов
 */
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService service;

    @Autowired
    public EventAdminController(EventAdminService service) {
        this.service = service;
    }

    /**
     * публикация события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @PatchMapping("{eventId}/publish")
    public EventFullDto publish(@Positive @PathVariable long eventId) {
        return service.publish(eventId);
    }

    /**
     * отклонение события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @PatchMapping("{eventId}/reject")
    public EventFullDto reject(@Positive @PathVariable long eventId) {
        return service.reject(eventId);
    }

    /**
     * обновление события администратором
     *
     * @param eventId - id события
     * @param dto     - dto объект с данными для обновления
     * @return - полный dto объект события
     */
    @PutMapping("/{eventId}")
    public EventFullDto update(@Positive @PathVariable long eventId, @RequestBody AdminUpdateEventRequest dto) {
        return service.update(eventId, dto);
    }
}
