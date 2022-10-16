package ru.yandex.practicum.service.services.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.*;
import ru.yandex.practicum.service.model.*;
import ru.yandex.practicum.service.model.dto.*;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.model.mappers.RequestMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.RequestRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * класс сервиса событий для пользователей
 */
@Service
@Slf4j
public class EventUsersServiceImpl implements EventUsersService {
    private final int createTimeLag = 2;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    @Lazy
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public EventUsersServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                                 EventMapper eventMapper, CategoryRepository categoryRepository,
                                 RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto dto) {
        validateUserId(userId);
        Event event = eventMapper.toEventFromNewEventDto(dto);
        if (!event.getCreatedOn().plusHours(createTimeLag).isBefore(event.getEventDate())) {
            throw new EventDateValidationException(createTimeLag);
        }
        event.setInitiator(userRepository.findById(userId).get());
        EventFullDto fullDto =
                eventMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие {} с id = {} было создано", fullDto.getTitle(), fullDto.getId());
        return fullDto;
    }

    @Override
    @Transactional
    public EventFullDto findEventByIdWhereUserIsOwner(long userId, long eventId) {
        validateUserId(userId);
        Event event = getEventByIdWhereUserIsOwner(userId, eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findAllWhereUserIsOwner(long userId, Integer from, Integer size) {
        validateUserId(userId);
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long userId, long eventId) {
        validateUserId(userId);
        Event event = getEventByIdWhereUserIsOwner(userId, eventId);
        if (event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
            eventRepository.save(event);
            log.info("Событие с id = {} было отменено", eventId);
            return eventMapper.toEventFullDto(event);
        } else {
            throw new EventStateValidationException();
        }
    }

    @Override
    public EventFullDto update(long userId, UpdateEventRequest dto) {
        validateUserId(userId);
        Event event = getEventByIdWhereUserIsOwner(userId, dto.getEventId());
        System.out.println(event.getState() + "время  " + event.getEventDate());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateValidationException();
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id = " + dto.getCategory() + " не найдена")));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            LocalDateTime startEvent = LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (LocalDateTime.now().plusHours(createTimeLag).isAfter(startEvent)) {
                throw new EventDateValidationException(createTimeLag);
            }
            event.setEventDate(startEvent);
        } else {
            if (LocalDateTime.now().plusHours(createTimeLag).isAfter(event.getEventDate())) {
                throw new EventDateValidationException(createTimeLag);
            }
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (!event.getState().equals(EventState.REVISION)) {
            event.setState(EventState.PENDING);
        }
        EventFullDto fullDto = eventMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id = {} изменено согласно данным {}", dto.getEventId(), dto);
        return fullDto;
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId) {
        validateUserId(userId);
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventOwnerValidationException(userId, eventId, " не является ");
        }
        if (eventRepository.existsById(eventId)) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(RequestMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Событие с id " + eventId + " не найдено.");
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        validateUserId(userId);
        ParticipationRequest request = requestRepository.findById(reqId).orElseThrow(() ->
                new NotFoundException("Запрос с id = " + reqId + " не найден."));
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventOwnerValidationException(userId, eventId, " не является ");
        }
        if (!isEventAvailable(eventId)) {
            throw new EventIsNotAvailableException(eventId);
        }
        request.setStatus(RequestStatus.CONFIRMED);
        ParticipationRequestDto dto = RequestMapper.toDto(requestRepository.save(request));
        log.info("Запрос с id = {} подтверждён", reqId);
        if (eventRepository.findParticipantLimitById(eventId) == requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED.toString()) && eventRepository.findParticipantLimitById(eventId) != 0) {
            requestRepository.findAllByEventIdAndNotStatus(eventId, RequestStatus.CONFIRMED.toString())
                    .forEach(r -> r.setStatus(RequestStatus.CANCELED));
        }
        return dto;
    }

    @Override
    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        validateUserId(userId);
        ParticipationRequest request = requestRepository.findById(reqId).orElseThrow(() ->
                new NotFoundException("Запрос с id = " + reqId + " не найден."));
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new EventOwnerValidationException(userId, eventId, " не является ");
        }
        request.setStatus(RequestStatus.REJECTED);
        ParticipationRequestDto dto = RequestMapper.toDto(requestRepository.save(request));
        log.info("Запрос с id = {} отклонён", reqId);
        return dto;
    }

    @Override
    public List<EventRevisionDto> findAllRevision(long userId) {
        validateUserId(userId);
        return eventRepository.findAllByInitiatorIdAndState(userId, EventState.REVISION).stream()
                .map(eventMapper::toEventRevisionDto)
                .collect(Collectors.toList());
    }

    /**
     * проверка существования пользователя по id
     *
     * @param userId - id пользователя
     */
    private void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    /**
     * возвращение события созданного пользователем по id пользователя и id события
     *
     * @param userId  - id пользователя
     * @param eventId - id события
     * @return - объект события
     */
    private Event getEventByIdWhereUserIsOwner(long userId, long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new EventOwnerValidationException(userId, eventId, " не является "));
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
