package ru.yandex.practicum.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.services.UsersService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private UsersService service;

    @Autowired
    public UsersController(UsersService service) {
        this.service = service;
    }

    /**
     * создание события
     * @param userId - id пользователя
     * @param dto - dto объект создаваемого события
     * @return - dto объект созданного события
     */
    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto dto) {
        return service.createEvent(userId, dto);
    }

    /**
     * запрос события созданного пользователем по id
     * @param userId - id пользователя
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByIdWhereUserIsOwner(@PathVariable long userId, @PathVariable long eventId) {
        return service.getEventByIdWhereUserIsOwner(userId, eventId);
    }
}
