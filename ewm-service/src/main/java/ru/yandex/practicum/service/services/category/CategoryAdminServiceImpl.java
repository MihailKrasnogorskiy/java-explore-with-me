package ru.yandex.practicum.service.services.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.CategoryUsedException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;
import ru.yandex.practicum.service.model.mappers.CategoryMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

/**
 * класс сервиса категорий для администраторов
 */
@Service
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryAdminServiceImpl(UserRepository repository, CategoryRepository categoryRepository,
                                    EventRepository eventRepository) {
        this.userRepository = repository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository
                .save(CategoryMapper.toCategoryFromNewCategoryDto(dto)));
        log.info("Категория {} с id = {} была создана", categoryDto.getName(), categoryDto.getId());
        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto dto) {
        if (categoryRepository.existsById(dto.getId())) {
            CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository
                    .save(CategoryMapper.toCategoryFromCategoryDto(dto)));
            log.info("Категория с id = {} была обновлена, новое имя - {}", categoryDto.getId(), categoryDto.getName());
            return categoryDto;
        } else {
            throw new NotFoundException(String.format("Категория с id = '%s' не найдена", dto.getId()));
        }
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        if (categoryRepository.existsById(id)) {
            if (eventRepository.findAllByCategoryId(id).isEmpty()) {
                categoryRepository.deleteById(id);
                log.info("Категория с id {} была удалена", id);
            } else {
                throw new CategoryUsedException(id);
            }
        } else {
            throw new NotFoundException(String.format("Категория с id = '%s' не найдена", id));
        }
    }
}
