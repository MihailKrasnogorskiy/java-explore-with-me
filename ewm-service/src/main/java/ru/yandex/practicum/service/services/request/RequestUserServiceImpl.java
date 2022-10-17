package ru.yandex.practicum.service.services.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.*;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.ParticipationRequest;
import ru.yandex.practicum.service.model.RequestStatus;
import ru.yandex.practicum.service.model.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.model.mappers.RequestMapper;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.RequestRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис запросов на участие в событиях для пользователей
 */
@Service
@Slf4j
public class RequestUserServiceImpl implements RequestUserService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RequestUserServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                                  EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = '%s' не найдено", eventId)));
        if (event.getInitiator().getId() == userId) {
            throw new EventOwnerValidationException(userId, eventId, " ");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateValidationException("На участие в неопубликованных событиях заявки не принимаются");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            RequestStatus status = requestRepository.findByEventIdAndRequesterId(eventId, userId).get().getStatus();
            if (status.equals(RequestStatus.CONFIRMED) || status.equals(RequestStatus.PENDING)) {
                throw new RepeatedRequestException();
            }
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(userRepository.findById(userId).orElseThrow(() ->
                        new NotFoundException(String.format("Пользователь с id '%s' не найден.", userId))))
                .event(event)
                .status(RequestStatus.PENDING)
                .build();
        if (!isEventAvailable(eventId)) {
            throw new EventIsNotAvailableException(eventId);
        }
        if (!event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipationRequestDto dto = RequestMapper.toDto(requestRepository.save(request));
        log.info("Заявка id = {} пользователя с id = {} на участие в событии с id = {} создана", dto.getId(), userId,
                eventId);
        return dto;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        validateUserId(userId);
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Заявка с id = '%s' не найдена", requestId)));
        if (!requestRepository.existsByIdAndRequesterId(requestId, userId)) {
            throw new RequestOwnerValidationException(userId, requestId);
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequestDto dto = RequestMapper.toDto(requestRepository.save(request));
        log.info("Заявка id = {} пользователя с id = {} отменена создателем", dto.getId(), userId);
        return dto;
    }

    @Override
    public List<ParticipationRequestDto> findAllByRequesterId(long userId) {
        validateUserId(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * проверка существования пользователя по id
     *
     * @param userId - id пользователя
     */
    private void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id '%s' не найден.", userId));
        }
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
