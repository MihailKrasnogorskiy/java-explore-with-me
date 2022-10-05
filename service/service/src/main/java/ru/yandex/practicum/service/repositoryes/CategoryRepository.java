package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.service.model.Category;
import ru.yandex.practicum.service.model.dto.CategoryDto;

import java.util.List;

/**
 * репозиторий категорий
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {
    /**
     * запрос списка всех категорий
     *
     * @param pageable - объект для создания запросов на перелистывание страниц
     * @return - список всех категорий
     */
    List<Category> findAll(Pageable pageable);
}
