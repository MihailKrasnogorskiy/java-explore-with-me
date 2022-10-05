package ru.yandex.practicum.service.services;

import ru.yandex.practicum.service.model.dto.CategoryDto;

import java.util.List;

/**
 * интерфейс сервис публичного контроллера
 */
public interface PublicService {
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
     * @param id - id dto объекта категории
     * @return - dto объект категории
     */
    CategoryDto findCategoryById(Long id);
}
