package ru.yandex.practicum.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.services.AdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

/**
 * класс контроллера для администраторов
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private AdminService service;

    @Autowired
    public AdminController(AdminService service) {
        this.service = service;
    }

    /**
     * создание пользователя
     *
     * @param dto - dto объект для создания пользователя
     * @return - полный dto объект пользователя
     */
    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody UserCreateDto dto) {
        return service.createUser(dto);
    }

    /**
     * удаление пользователя
     *
     * @param id - id пользователя
     */
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        service.deleteUser(id);
    }

    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam List<Long> ids,
                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.findUsers(ids, from, size);
    }
}
