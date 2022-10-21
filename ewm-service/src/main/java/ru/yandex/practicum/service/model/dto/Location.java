package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * класс локации
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private float lat;
    private float lon;
}
