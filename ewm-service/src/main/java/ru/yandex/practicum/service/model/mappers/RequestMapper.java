package ru.yandex.practicum.service.model.mappers;

import ru.yandex.practicum.service.model.ParticipationRequest;
import ru.yandex.practicum.service.model.dto.ParticipationRequestDto;

import java.time.format.DateTimeFormatter;

/**
 * маппер запросов на участие в событии
 */
public class RequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(request.getStatus())
                .build();
    }
}
