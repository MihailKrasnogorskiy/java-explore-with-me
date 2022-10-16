package ru.yandex.practicum.service.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.NewUserRequest;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.services.user.UserAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * класс контроллера пользователей для администраторов
 */
@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserAdminService service;

    @Autowired
    public UserAdminController(UserAdminService service) {
        this.service = service;
    }

    /**
     * создание пользователя
     *
     * @param dto - dto объект для создания пользователя
     * @return - полный dto объект пользователя
     */
    @PostMapping()
    public UserDto createUser(@Valid @RequestBody NewUserRequest dto) {
        return service.createUser(dto);
    }

    /**
     * удаление пользователя
     *
     * @param id - id пользователя
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        service.deleteUser(id);
    }

    /**
     * запрос списка пользователей
     *
     * @param ids  - id пользователей
     * @param from - количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size - количество элементов в наборе
     * @return - список пользователей
     */

    @GetMapping()
    public List<UserDto> findUsers(@RequestParam List<Long> ids,
                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.findUsers(ids, from, size);
    }
}
