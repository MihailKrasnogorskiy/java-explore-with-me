package ru.yandex.practicum.service.services.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.EventDateValidationException;
import ru.yandex.practicum.service.exceptions.EventStateValidationException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;
import ru.yandex.practicum.service.repositoryes.EventCustomCriteriaRepository;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.services.StatisticService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис событий для администраторов
 */
@Service
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {
    private final int publishTimeLag = 1;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    private final EventCustomCriteriaRepository criteriaRepository;
    private final StatisticService statisticService;

    @Autowired
    public EventAdminServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository,
                                 EventMapper eventMapper, EventCustomCriteriaRepository criteriaRepository,
                                 StatisticService statisticService) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.criteriaRepository = criteriaRepository;
        this.statisticService = statisticService;
    }

    @Override
    @Transactional
    public EventFullDto publish(long eventId) {
        Event event = getEventById(eventId);
        if (!event.getCreatedOn().plusHours(publishTimeLag).isBefore(event.getEventDate())) {
            throw new EventDateValidationException(publishTimeLag);
        }
        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);
        log.info("Событие с id = {} опубликовано", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto reject(long eventId) {
        Event event = getEventById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateValidationException();
        }
        event.setState(EventState.CANCELED);
        log.info("Событие с id = {} отклонено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto update(long eventId, AdminUpdateEventRequest dto) {
        Event event = getEventById(eventId);
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Категория с id = '%s' не найдена", dto.getCategory()))));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        EventFullDto fullDto = eventMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id = {} изменено согласно данным {}", eventId, dto);
        return fullDto;
    }

    @Override
    public List<EventFullDto> findAll(List<Long> users, List<EventState> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Event> events = criteriaRepository.findAll(null, categories, null, start, end, from, size, users, states);
        List<Event> eventsWithViews = statisticService.getStatistic(events);
        return eventsWithViews.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revision(long eventId, String comment) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventStateValidationException("Нельзя отправить на доработку событие не ожидающее модерации");
        }
        event.setState(EventState.REVISION);
        event.setAdminComment(comment);
        log.info("Событие с id = {} отправлено на доработку", eventId);
    }

    /**
     * получение события по id
     *
     * @param eventId - id события
     * @return - объект события
     */
    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = '%s' не найдено", eventId)));
    }
}
