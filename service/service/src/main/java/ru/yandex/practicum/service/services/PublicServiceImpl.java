package ru.yandex.practicum.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.OffsetLimitPageable;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.mappers.CategoryMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * класс публичного сервиса
 */
@Service
public class PublicServiceImpl implements PublicService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public PublicServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoryById(Long id) {
        if (categoryRepository.existsById(id)) {
            return CategoryMapper.toDto(categoryRepository.findById(id).get());
        } else {
            throw new NotFoundException("Категория с id = " + id + " не найдена");
        }
    }
}
