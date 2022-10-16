package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * класс dto для создания сообщения обратной связи
 */

@Data
@AllArgsConstructor
public class FeedbackPostCreateDto {

    @NotBlank
    private String userName;
    @NotBlank
    private String post;
}