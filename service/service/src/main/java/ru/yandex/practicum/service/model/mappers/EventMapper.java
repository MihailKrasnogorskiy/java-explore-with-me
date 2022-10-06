package ru.yandex.practicum.service.model.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.dto.EventFullDto;
import ru.yandex.practicum.service.model.dto.EventShortDto;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * маппер событий
 */
@Component
public class EventMapper {

    private final CategoryRepository categoryRepository;

    @Autowired
    public EventMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
                .views(0)
                .publishedOn(null)
                .state(EventState.PENDING)
                .build();
    }

    /**
     * создание полного dto объекта события из объекта события
     *
     * @param event - объект события
     * @return - полный dto объект события
     */
    public EventFullDto toEventFullDto(Event event) {
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
                .build();
    }

    /**
     * создание короткого dto объекта события из объекта события
     *
     * @param event - объект события
     * @return - короткий dto объект события
     */

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .title(event.getTitle())
                .paid(event.isPaid())
                .views(event.getViews())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .build();
    }
}
