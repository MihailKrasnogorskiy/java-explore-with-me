package ru.yandex.practicum.service.model.mappers;

import ru.yandex.practicum.service.model.FeedbackPost;
import ru.yandex.practicum.service.model.dto.FeedbackPostAdminDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostPublicDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * маппер сообщений обратной связи
 */
public class FeedbackPostMapper {
    /**
     * преобразование в объект сообщения обратной связи
     *
     * @param dto - dto объект для создания сообщения обратной связи
     * @return - объект сообщения обратной связи
     */
    public static FeedbackPost toFeedbackPost(FeedbackPostCreateDto dto) {
        return FeedbackPost.builder()
                .userName(dto.getUserName())
                .post(dto.getPost())
                .created(LocalDateTime.now())
                .build();
    }

    /**
     * преобразование в публичный dto объект сообщения обратной связи
     *
     * @param post - объект сообщения обратной связи
     * @return - публичный dto объект сообщения обратной связи
     */
    public static FeedbackPostPublicDto toPublicDto(FeedbackPost post) {
        return FeedbackPostPublicDto.builder()
                .userName(post.getUserName())
                .post(post.getPost())
                .created(post.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * преобразование в dto объект сообщения обратной связи для администраора
     *
     * @param post - объект сообщения обратной связи
     * @return - dto объект сообщения обратной связи для администратора
     */
    public static FeedbackPostAdminDto toAdminDto(FeedbackPost post) {
        return FeedbackPostAdminDto.builder()
                .id(post.getId())
                .userName(post.getUserName())
                .post(post.getPost())
                .created(post.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .isPublish(post.isPublish())
                .build();
    }
}
