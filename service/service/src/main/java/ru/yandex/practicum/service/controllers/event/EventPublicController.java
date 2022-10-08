package ru.yandex.practicum.service.controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.exceptions.EventUnknownSortException;
import ru.yandex.practicum.service.model.SortMethod;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.services.event.EventPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * публичный контроллер событий
 */
@RestController
@RequestMapping("/events")
public class EventPublicController {

    private final EventPublicService service;

    @Autowired
    public EventPublicController(EventPublicService service) {
        this.service = service;
    }

    /**
     * запрос событий с возможностью фильтрации
     *
     * @param text          - текст для поиска в содержимом аннотации и подробном описании события
     * @param categories    - список идентификаторов категорий в которых будет вестись поиск
     * @param paid          - поиск только платных/бесплатных событий
     * @param rangeStart    - дата и время не раньше которых должно произойти событие
     * @param rangeEnd      - дата и время не позже которых должно произойти событие
     * @param onlyAvailable - только события у которых не исчерпан лимит запросов на участие
     * @param sortMethod    - вариант сортировки
     * @param from          - количество событий, которые нужно пропустить для формирования текущего набора
     * @param size          - количество событий в наборе
     * @return - список событий
     */

    @GetMapping
    public List<EventFullDto> findAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) SortMethod sortMethod,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        if (sortMethod!=null && sortMethod.equals(SortMethod.UNSUPPORTED_METHOD)) {
            throw new EventUnknownSortException();
        }
        return service.findAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sortMethod, from, size);
    }

    /**
     * запрос опубликованного события
     *
     * @param eventId - id события
     * @return - полный dto объект события
     */
    @GetMapping("{eventId}")
    public EventFullDto findById(@Positive @PathVariable long eventId) {
        return service.findById(eventId);
    }

}
