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
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.repositoryes.EventRepository;

import java.time.LocalDateTime;

/**
 * сервис событий для администраторов
 */
@Service
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {
    private final int PUBLISH_TIME_LAG = 1;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventAdminServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (!event.getCreatedOn().plusHours(PUBLISH_TIME_LAG).isBefore(event.getEventDate())) {
            throw new EventDateValidationException(PUBLISH_TIME_LAG);
        }
        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);
        log.info("Событие с id = {} опубликовано", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateValidationException();
        }
        event.setState(EventState.CANCELED);
        log.info("Событие с id = {} отклонено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    /**
     * проверка на существование события по id
     *
     * @param eventId - id события
     */
    private void validateEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id = " + eventId + " не найдено");
        }
    }
}
