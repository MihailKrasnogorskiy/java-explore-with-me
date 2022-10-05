package ru.yandex.practicum.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.EventDateValidationException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

/**
 * класс сервиса пользователей
 */
@Service
@Slf4j
public class UsersServiceImpl implements UsersService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    @Lazy
    private EventMapper eventMapper;

    private final int CREATE_TIME_LAG = 2;

    @Autowired
    public UsersServiceImpl(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto dto) {
        validateUserId(userId);
        Event event = eventMapper.toEventFromNewEventDto(dto);
        if(!event.getCreatedOn().plusHours(CREATE_TIME_LAG).isBefore(event.getEventDate())){
            throw new EventDateValidationException(CREATE_TIME_LAG);
        }
        event.setInitiator(userRepository.findById(userId).get());
        EventFullDto fullDto =
                eventMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие {} с id = {} было создано", fullDto.getTitle(), fullDto.getId());
        return fullDto;
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdWhereUserIsOwner(long userId, long eventId) {
        validateUserId(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не создавал событие с id = " + eventId));
        return eventMapper.toEventFullDto(event);
    }

    private void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }
}
