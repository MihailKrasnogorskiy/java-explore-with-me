package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.*;
import ru.yandex.practicum.service.services.event.EventUsersService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * контроллер событий для пользователей
 */
@RestController
@RequestMapping("/users/{userId}/events")
@Validated
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
    public EventFullDto createEvent(@Positive @PathVariable long userId, @Valid @RequestBody NewEventDto dto) {
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
    public EventFullDto getEventByIdWhereUserIsOwner(@Positive @PathVariable long userId,
                                                     @Positive @PathVariable long eventId) {
        return service.findEventByIdWhereUserIsOwner(userId, eventId);
    }

    /**
     * получение списка событий отправленных на доработку администратором
     *
     * @param userId - id пользователя
     * @return - список событий отправленных на доработку администратором
     */
    @GetMapping("/revision")
    public List<EventRevisionDto> getAllRevision(@Positive @PathVariable long userId) {
        return service.findAllRevision(userId);
    }

    /**
     * получение событий, добавленных текущим пользователем
     *
     * @param userId - id пользователя
     * @param from   - количество подборок, которые нужно пропустить для формирования текущего набора
     * @param size   - количество подборок в наборе
     * @return - список коротких dto событий
     */
    @GetMapping()
    public List<EventShortDto> findAllWhereUserIsOwner(@Positive @PathVariable long userId,
                                                       @PositiveOrZero @RequestParam(name = "from",
                                                               defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size",
                                                               defaultValue = "10") Integer size) {
        return service.findAllWhereUserIsOwner(userId, from, size);
    }

    /**
     * отмена события пользователем
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @PatchMapping("{eventId}")
    public EventFullDto cancelEvent(@Positive @PathVariable long userId, @Positive @PathVariable long eventId) {
        return service.cancelEvent(userId, eventId);
    }

    /**
     * обновление события пользователем
     *
     * @param userId - id пользователя
     * @param dto    - dto объект с данными для обновления
     * @return - полный dto объект события
     */
    @PatchMapping()
    public EventFullDto update(@Positive @PathVariable long userId, @Valid @RequestBody UpdateEventRequest dto) {
        return service.update(userId, dto);
    }

    /**
     * получение списка всех запросов на участие в событии пользователя
     *
     * @param userId - id пользователя
     * @return - список запросов
     */
    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsByEventId(@Positive @PathVariable long userId,
                                                                  @Positive @PathVariable long eventId) {
        return service.findAllRequestsByEventId(userId, eventId);
    }

    /**
     * подтверждение запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param reqId   - id запроса
     * @param eventId - id события
     * @return - dto объект запроса
     */
    @PatchMapping("{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@Positive @PathVariable long userId,
                                                  @Positive @PathVariable long eventId,
                                                  @Positive @PathVariable long reqId) {
        return service.confirmRequest(userId, eventId, reqId);
    }

    /**
     * отклонение запроса на участие в событии
     *
     * @param userId  - id пользователя
     * @param reqId   - id запроса
     * @param eventId - id события
     * @return - dto объект запроса
     */
    @PatchMapping("{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@Positive @PathVariable long userId,
                                                 @Positive @PathVariable long eventId,
                                                 @Positive @PathVariable long reqId) {
        return service.rejectRequest(userId, eventId, reqId);
    }
}
