package ru.yandex.practicum.service.controllers.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.services.category.CategoryPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * публичный контроллер категорий
 */
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryPublicController {

    private final CategoryPublicService service;

    @Autowired
    public CategoryPublicController(CategoryPublicService service) {
        this.service = service;
    }

    /**
     * получение списка всех категорий
     *
     * @param from - количество категорий, которые нужно пропустить для формирования текущего набора
     * @param size - количество категорий в наборе
     * @return список всех категорий
     */
    @GetMapping()
    public List<CategoryDto> findAllCategories(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.findAllCategories(from, size);
    }

    /**
     * получение информации о категории по id
     *
     * @param id - id категории
     * @return - dto объект категории
     */
    @GetMapping("/{id}")
    public CategoryDto findCategoryById(@PathVariable Long id) {
        return service.findCategoryById(id);
    }
}
