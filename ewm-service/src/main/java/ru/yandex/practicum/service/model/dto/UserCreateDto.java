package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * класс dto для создания пользователя
 */
@Data
@AllArgsConstructor
public class UserCreateDto {
    @NotNull
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
