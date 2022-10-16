package ru.yandex.practicum.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * класс dto сообщения обратной связи для администратора
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPostAdminDto {
    private long id;
    private String userName;
    private String post;
    private String created;
    private boolean isPublish;
}