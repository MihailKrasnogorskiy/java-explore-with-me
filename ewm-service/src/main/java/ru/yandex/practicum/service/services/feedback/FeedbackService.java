package ru.yandex.practicum.service.services.feedback;

import ru.yandex.practicum.service.model.dto.FeedbackPostAdminDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostPublicDto;

import java.util.List;

/**
 * интерфейс сервиса работы с обратной связью
 */
public interface FeedbackService {
    /**
     * создание сообщения обратной связи
     *
     * @param dto - объект для создания сообщения обратной связи
     */
    void createFeedbackPost(FeedbackPostCreateDto dto);

    /**
     * запрос списка сообщений обратной связи для публикации на сайте
     *
     * @return список сообщений обратной связи для публикации на сайте
     */
    List<FeedbackPostPublicDto> findAllFeedbackForPublish();

    /**
     * запрос всех сообщений обратной связи
     *
     * @param days - количество предшествующих дней за которые надо собрать сообщения
     * @return - список сообщений обратной связи
     */
    List<FeedbackPostAdminDto> findAllFeedback(Integer days);

    /**
     * одобрение публикации сообщения обратной связи
     *
     * @param id - id сообщения обратной связи
     * @return dto объект сообщения обратной связи
     */
    FeedbackPostAdminDto publishFeedbackPost(long id);

    /**
     * удаление сообщения обратной связи
     *
     * @param id - id сообщения обратной связи
     */
    void deleteFeedbackPost(long id);
}
