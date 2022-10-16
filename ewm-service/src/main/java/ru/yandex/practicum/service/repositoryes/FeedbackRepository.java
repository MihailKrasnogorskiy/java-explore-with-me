package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.model.FeedbackPost;

import java.time.LocalDateTime;
import java.util.List;

/**
 * репозиторий сообщений обратной связи
 */
@Component
public interface FeedbackRepository extends CrudRepository<FeedbackPost, Long> {
    List<FeedbackPost> findAllByPublish(boolean isPublish);

    List<FeedbackPost> findAllByCreatedAfter(LocalDateTime from);
}
