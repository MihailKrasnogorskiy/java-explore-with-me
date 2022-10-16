package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * класс dto сообщения обратной связи для публикации на сайте
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPostPublicDto {
    private String userName;
    private String post;
    private String created;
}