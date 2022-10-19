package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.Category;

import java.util.List;

/**
 * репозиторий категорий
 */
@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    /**
     * запрос списка всех категорий
     *
     * @param pageable - объект для создания запросов на перелистывание страниц
     * @return - список всех категорий
     */
    List<Category> findAll(Pageable pageable);
}
