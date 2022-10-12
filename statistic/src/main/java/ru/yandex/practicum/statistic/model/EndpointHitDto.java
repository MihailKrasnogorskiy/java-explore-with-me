package ru.yandex.practicum.statistic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * dto класс просмотра эндпоинта
 */
@Data
@Builder
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank
    @NotNull
    private String app;
    @NotBlank
    @NotNull
    private String uri;
    @NotBlank
    @NotNull
    private String ip;
    @NotBlank
    @NotNull
    private String timestamp;

    public EndpointHitDto() {
    }
}
