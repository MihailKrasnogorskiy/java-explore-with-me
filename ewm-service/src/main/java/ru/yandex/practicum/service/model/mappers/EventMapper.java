package ru.yandex.practicum.service.model.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.RequestStatus;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.EventRevisionDto;
import ru.yandex.practicum.service.model.dto.EventShortDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.services.StatisticService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * маппер событий
 */
@Component
public class EventMapper {

    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatisticService statisticService;

    @Autowired
    public EventMapper(CategoryRepository categoryRepository, RequestRepository requestRepository,
                       StatisticService statisticService) {
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statisticService = statisticService;
    }

    /**
     * создание объекта события из dto объекта для создания события
     *
     * @param dto - dto объект для создания события
     * @return - объект события
     */
    public Event toEventFromNewEventDto(NewEventDto dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(categoryRepository.findById(dto.getCategory()).get())
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(dto.getDescription())
                .title(dto.getTitle())
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .views(0L)
                .publishedOn(null)
                .state(EventState.PENDING)
                .build();
    }

    /**
     * создание полного dto объекта события из объекта события
     *
     * @param eventWithoutViews - объект события
     * @return - полный dto объект события
     */
    public EventFullDto toEventFullDto(Event eventWithoutViews) {
        List<Event> eventsWithoutViews = new ArrayList<>();
        eventsWithoutViews.add(eventWithoutViews);
        List<Event> events = statisticService.getStatistic(eventsWithoutViews);
        Event event = events.get(0);
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(event.getDescription())
                .title(event.getTitle())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .views(event.getViews())
                .publishedOn(event.getPublishedOn() != null ?
                        event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .state(event.getState())
                .confirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED))
                .build();
    }

    /**
     * создание короткого dto объекта события из объекта события
     *
     * @param eventWithoutViews - объект события
     * @return - короткий dto объект события
     */
    @Transactional
    public EventShortDto toEventShortDto(Event eventWithoutViews) {
        List<Event> eventsWithoutViews = new ArrayList<>();
        eventsWithoutViews.add(eventWithoutViews);
        List<Event> events = statisticService.getStatistic(eventsWithoutViews);
        Event event = events.get(0);
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .title(event.getTitle())
                .paid(event.isPaid())
                .views(event.getViews())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .confirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED))
                .build();
    }

    /**
     * создание dto объекта события отправленного на доработку администратором из объекта события
     *
     * @param event - объект события
     * @return - dto объект события отправленного на доработку администратором
     */
    public EventRevisionDto toEventRevisionDto(Event event) {
        return EventRevisionDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(event.getDescription())
                .title(event.getTitle())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .id(event.getId())
                .state(event.getState())
                .adminComment(event.getAdminComment())
                .build();
    }
}
