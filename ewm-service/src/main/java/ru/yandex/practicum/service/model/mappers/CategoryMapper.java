package ru.yandex.practicum.service.model.mappers;

import ru.yandex.practicum.service.model.Category;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;

/**
 * маппер категорий
 */
public class CategoryMapper {
    /**
     * преобразование в объект категории из dto объекта для создания категории
     *
     * @param dto - dto объект для создания категории
     * @return - объект категории
     */
    public static Category toCategoryFromNewCategoryDto(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    /**
     * преобразование в объект категории из dto объекта категории
     *
     * @param dto - dto объект категории
     * @return - объект категории
     */
    public static Category toCategoryFromCategoryDto(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    /**
     * преобразование в dto объект категории
     *
     * @param category - объект категории
     * @return - dto объект категории
     */
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
