package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.services.event.EventAdminService;

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
    public EventFullDto publishedEvent(@PathVariable long eventId) {
        return service.publishEvent(eventId);
    }

    /**
     * отклонение события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @PatchMapping("{eventId}/reject")
    public EventFullDto rejectedEvent(@PathVariable long eventId) {
        return service.rejectEvent(eventId);
    }
}
