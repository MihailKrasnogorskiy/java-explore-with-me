package ru.yandex.practicum.service.controllers.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;
import ru.yandex.practicum.service.model.dto.FeedbackPostPublicDto;
import ru.yandex.practicum.service.services.feedback.FeedbackService;

import java.util.List;

/**
 * публичный контроллер работы с обратной связью
 */
@RestController
@RequestMapping("/feedback")
@Validated
public class FeedbackPublicController {

    private final FeedbackService service;

    @Autowired
    public FeedbackPublicController(FeedbackService service) {
        this.service = service;
    }

    /**
     * создание сообщения обратной связи
     *
     * @param dto - объект для создания сообщения обратной связи
     */
    @PostMapping("/feedback")
    public void createFeedbackPost(@RequestBody FeedbackPostCreateDto dto) {
        service.createFeedbackPost(dto);
    }

    /**
     * запрос списка сообщений обратной связи для публикации на сайте
     *
     * @return список сообщений обратной связи для публикации на сайте
     */
    @GetMapping("/feedback")
    public List<FeedbackPostPublicDto> findAllFeedback() {
        return service.findAllFeedbackForPublish();
    }
}
