package ru.yandex.practicum.service.services.category;

import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;

/**
 * интерфейс сервиса категорий для администраторов
 */
public interface CategoryAdminService {

    /**
     * создание категории
     *
     * @param dto - dto объект для создания категории
     * @return - dto объект категории
     */
    CategoryDto createCategory(NewCategoryDto dto);

    /**
     * обновление категории
     *
     * @param dto - dto объект для обновления категории
     * @return - dto объект категории
     */
    CategoryDto updateCategory(CategoryDto dto);

    /**
     * удаление категории
     *
     * @param id - id категории
     */
    void deleteCategory(long id);
}
