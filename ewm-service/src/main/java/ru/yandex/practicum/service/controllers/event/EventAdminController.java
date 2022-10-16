package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.services.event.EventAdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

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
     * отправление события на доработку
     *
     * @param eventId - id события
     * @param comment - комментарий администратора
     */
    @PatchMapping("{eventId}/revision")
    public void revision(@Positive @PathVariable long eventId, @RequestBody String comment) {
        service.revision(eventId, comment);
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

    /**
     * запрос событий с возможностью фильтрации
     *
     * @param users      - список id пользователей, чьи события нужно найти
     * @param states     - список состояний в которых находятся искомые события
     * @param categories - список идентификаторов категорий в которых будет вестись поиск
     * @param rangeStart - дата и время не раньше которых должно произойти событие
     * @param rangeEnd   - дата и время не позже которых должно произойти событие
     * @param from       - количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       - количество событий в наборе
     * @return - список событий
     */
    @GetMapping
    public List<EventFullDto> findAll(@RequestParam(required = false) List<Long> users,
                                      @RequestParam(required = false) List<EventState> states,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return service.findAll(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
