package ru.yandex.practicum.service.controllers.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.services.request.RequestUserService;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * контроллер запросов на участие в событиях для пользователей
 */
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestUserController {

    private final RequestUserService service;

    @Autowired
    public RequestUserController(RequestUserService service) {
        this.service = service;
    }

    /**
     * создание запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - dto объект запроса на участие
     */
    @PostMapping()
    //    @ResponseStatus(HttpStatus.CREATED) - в api требуется вернуть статус 201, а в тестах ждут 200, ждём разъяснений
    public ParticipationRequestDto create(@Positive @PathVariable long userId,
                                          @Positive @RequestParam(name = "eventId") long eventId) {
        return service.create(userId, eventId);
    }

    /**
     * отмена запроса на участие в событии
     *
     * @param userId    - id пользователя
     * @param requestId - id запроса
     * @return - dto объект запроса на участие
     */
    @PatchMapping("{requestId}/cancel")
    public ParticipationRequestDto cancel(@Positive @PathVariable long userId, @Positive @PathVariable long requestId) {
        return service.cancel(userId, requestId);
    }

    /**
     * получение списка всех запросов пользователя на участие в чужих событиях
     *
     * @param userId - id пользователя
     * @return - список запросов
     */
    @GetMapping()
    public List<ParticipationRequestDto> findAllByRequesterId(@Positive @PathVariable long userId) {
        return service.findAllByRequesterId(userId);
    }
}
