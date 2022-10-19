package ru.yandex.practicum.service.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.FeedbackPost;

import java.time.LocalDateTime;
import java.util.List;

/**
 * репозиторий сообщений обратной связи
 */
@Repository
public interface FeedbackRepository extends CrudRepository<FeedbackPost, Long> {
    List<FeedbackPost> findAllByIsPublish(Boolean isPublish);

    List<FeedbackPost> findAllByCreatedAfter(LocalDateTime from);
}
