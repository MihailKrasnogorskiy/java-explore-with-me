package ru.yandex.practicum.service.services.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.RequestStatus;
import ru.yandex.practicum.service.model.SortMethod;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.repositoryes.EventCustomCriteriaRepository;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.RequestRepository;
import ru.yandex.practicum.service.services.StatisticService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис публичного контроллера событий
 */
@Service
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventCustomCriteriaRepository criteriaRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final RequestRepository requestRepository;
    private final StatisticService statisticService;

    @Autowired
    public EventPublicServiceImpl(EventCustomCriteriaRepository criteriaRepository, EventRepository eventRepository,
                                  EventMapper eventMapper, RequestRepository requestRepository,
                                  StatisticService statisticService) {
        this.criteriaRepository = criteriaRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.requestRepository = requestRepository;
        this.statisticService = statisticService;
    }

    @Override
    public List<EventFullDto> findAll(String text, List<Long> categories, Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable, SortMethod sortMethod, Integer from,
                                      Integer size) {
        LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<EventState> states = new ArrayList<>();
        states.add(EventState.PUBLISHED);
        List<Event> eventsWithOutViews = criteriaRepository.findAll(text, categories, paid, start, end, from, size,
                null, states);
        if (onlyAvailable) {
            eventsWithOutViews.removeIf(event -> !isEventAvailable(event.getId()));
        }
        List<Event> events = statisticService.getStatistic(eventsWithOutViews);
        if (sortMethod != null) {
            if (sortMethod.equals(SortMethod.EVENT_DATE)) {
                return events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(eventMapper::toEventFullDto)
                        .collect(Collectors.toList());
            } else {
                return events.stream()
                        .sorted(Comparator.comparingLong(Event::getViews))
                        .map(eventMapper::toEventFullDto)
                        .collect(Collectors.toList());
            }
        }
        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findById(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() ->
                new NotFoundException("Событие с id = " + " не найдено."));
        List<Event> eventsWithoutViews = new ArrayList<>();
        eventsWithoutViews.add(event);
        List<Event> events = statisticService.getStatistic(eventsWithoutViews);
        log.info("Количество просмотров {}", events.get(0).getViews());
        return eventMapper.toEventFullDto(events.get(0));
    }

    /**
     * проверка возможности участия в событии
     *
     * @param eventId - id события
     * @return boolean
     */
    private boolean isEventAvailable(long eventId) {
        return (eventRepository.findParticipantLimitById(eventId) - requestRepository
                .countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED.toString()) > 0) ||
                eventRepository.findParticipantLimitById(eventId) == 0;
    }
}
