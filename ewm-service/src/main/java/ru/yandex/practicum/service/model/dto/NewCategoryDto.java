package ru.yandex.practicum.service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * класс dto для создания категорий
 */
@Data
@NoArgsConstructor
public class NewCategoryDto {
    @NotNull
    @NotBlank
    private String name;
}
