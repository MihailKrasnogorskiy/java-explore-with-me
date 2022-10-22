package ru.yandex.practicum.service.services.feedback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.FeedbackPost;
import ru.yandex.practicum.service.model.dto.FeedbackPostAdminDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostPublicDto;
import ru.yandex.practicum.service.model.mappers.FeedbackPostMapper;
import ru.yandex.practicum.service.repositories.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис работы с обратной связью
 */

@Service
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository repository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createFeedbackPost(FeedbackPostCreateDto dto) {
        FeedbackPost post = repository.save(FeedbackPostMapper.toFeedbackPost(dto));
        log.info("Сообщение обратной связи с id {} создано", post.getId());
    }

    @Override
    public List<FeedbackPostPublicDto> findAllFeedbackForPublish() {
        return repository.findAllByIsPublish(true).stream()
                .map(FeedbackPostMapper::toPublicDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackPostAdminDto> findAllFeedback(Integer days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        return repository.findAllByCreatedAfter(from).stream()
                .map(FeedbackPostMapper::toAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FeedbackPostAdminDto publishFeedbackPost(long id) {
        FeedbackPost post = repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Сообщение обратной связи с id = '%s' не найдено", id)));
        post.setPublish(true);
        log.info("Сообщение обратной связи с id {} одобрено к публикации", id);
        return FeedbackPostMapper.toAdminDto(post);
    }

    @Override
    @Transactional
    public void deleteFeedbackPost(long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("Сообщение обратной связи с id = '%s' не найдено", id));
        }
        repository.deleteById(id);
        log.info("Сообщение обратной связи с id {} удалено", id);
    }
}
