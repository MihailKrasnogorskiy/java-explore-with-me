package ru.yandex.practicum.service.services.category;

import ru.yandex.practicum.service.model.dto.CategoryDto;

import java.util.List;

/**
 * интерфейс публичного сервиса категорий
 */
public interface CategoryPublicService {
    /**
     * получение списка всех категорий
     *
     * @param from - количество категорий, которые нужно пропустить для формирования текущего набора
     * @param size - количество категорий в наборе
     * @return список всех категорий
     */
    List<CategoryDto> findAllCategories(Integer from, Integer size);

    /**
     * получение dto объекта категории по id
     *
     * @param id - id категории
     * @return - dto объект категории
     */
    CategoryDto findCategoryById(Long id);
}
