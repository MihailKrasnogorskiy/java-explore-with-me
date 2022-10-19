package ru.yandex.practicum.service.controllers.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;
import ru.yandex.practicum.service.services.category.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * класс контроллера категорий для администраторов
 */
@RestController
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryAdminController {
    private final CategoryAdminService service;

    @Autowired
    public CategoryAdminController(CategoryAdminService service) {
        this.service = service;
    }

    /**
     * создание категории
     *
     * @param dto - dto объект для создания категории
     * @return - dto объект категории
     */
    @PostMapping()
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto dto) {
        return service.createCategory(dto);
    }


    /**
     * обновление категории
     *
     * @param dto - dto объект для обновления категории
     * @return - dto объект категории
     */
    @PatchMapping()
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto dto) {
        return service.updateCategory(dto);
    }

    /**
     * удаление категории
     *
     * @param id - id категории
     */
    @DeleteMapping("/{id}")
    public void deleteCategory(@Positive @PathVariable long id) {
        service.deleteCategory(id);
    }

}
