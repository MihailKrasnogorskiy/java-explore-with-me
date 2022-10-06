package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.services.event.EventUsersService;

import javax.validation.Valid;

/**
 * контроллер событий для пользователей
 */
@RestController
@RequestMapping("/users/{userId}/events")
public class EventUsersController {

    private final EventUsersService service;

    @Autowired
    public EventUsersController(EventUsersService service) {
        this.service = service;
    }

    /**
     * создание события
     *
     * @param userId - id пользователя
     * @param dto    - dto объект создаваемого события
     * @return - dto объект созданного события
     */
    @PostMapping()
    public EventFullDto createEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto dto) {
        return service.createEvent(userId, dto);
    }

    /**
     * запрос события созданного пользователем по id
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @GetMapping("{eventId}")
    public EventFullDto getEventByIdWhereUserIsOwner(@PathVariable long userId, @PathVariable long eventId) {
        return service.getEventByIdWhereUserIsOwner(userId, eventId);
    }
}
