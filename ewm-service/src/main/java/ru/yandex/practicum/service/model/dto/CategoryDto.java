package ru.yandex.practicum.service.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * класс dto категорий
 */

@Data
@Builder
public class CategoryDto {
    @Min(value = 0)
    private long id;
    @NotNull
    @NotBlank
    private String name;
}
