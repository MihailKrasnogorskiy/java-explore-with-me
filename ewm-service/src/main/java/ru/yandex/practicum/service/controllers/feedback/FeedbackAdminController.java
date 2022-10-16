package ru.yandex.practicum.service.controllers.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.FeedbackPostAdminDto;
import ru.yandex.practicum.service.services.feedback.FeedbackService;

import java.util.List;

/**
 * контроллер работы с обратной связью для администраторов
 */
@RestController
@RequestMapping("/admin/feedback")
public class FeedbackAdminController {
    private final FeedbackService service;

    @Autowired
    public FeedbackAdminController(FeedbackService service) {
        this.service = service;
    }

    /**
     * запрос всех сообщений обратной связи
     *
     * @param days - количество предшествующих дней за которые надо собрать сообщения
     * @return - список сообщений обратной связи
     */
    @GetMapping()
    public List<FeedbackPostAdminDto> findAllFeedback(@RequestParam(name = "days", defaultValue = "1") Integer days) {
        return service.findAllFeedback(days);
    }

    /**
     * одобрение публикации сообщения обратной связи
     * @param id - id сообщения обратной связи
     * @return dto объект сообщения обратной связи
     */
    @PatchMapping("/{id}")
    public FeedbackPostAdminDto publishFeedbackPost(@PathVariable long id){
        return service.publishFeedbackPost(id);
    }

    /**
     * удаление сообщения обратной связи
     *
     * @param id - id сообщения обратной связи
     */
    @DeleteMapping("/{id}")
    public void deleteFeedbackPost(@PathVariable long id){
        service.deleteFeedbackPost(id);
    }
}
