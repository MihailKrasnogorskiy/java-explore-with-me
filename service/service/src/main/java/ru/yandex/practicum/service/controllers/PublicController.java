package ru.yandex.practicum.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.services.PublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class PublicController {

    private final PublicService service;

    @Autowired
    public PublicController(PublicService service) {
        this.service = service;
    }

    /**
     * получение списка всех категорий
     *
     * @param from - количество категорий, которые нужно пропустить для формирования текущего набора
     * @param size - количество категорий в наборе
     * @return список всех категорий
     */
    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.findAllCategories(from, size);
    }

    /**
     * получение информации о категории по id
     *
     * @param id - id dto объекта категории
     * @return - dto объект категории
     */
    @GetMapping("/categories/{id}")
    public CategoryDto findCategoryById(@PathVariable Long id) {
        return service.findCategoryById(id);
    }
}
