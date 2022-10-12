package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.service.model.RequestStatus;

/**
 * класс dto запросов на участие в событии
 */
@Data
@Builder
public class ParticipationRequestDto {
    private String created;
    private long event;
    private long id;
    private long requester;
    private RequestStatus status;
}
