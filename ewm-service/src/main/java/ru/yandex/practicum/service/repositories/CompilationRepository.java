package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.service.model.Compilation;

import java.util.List;

/**
 * интерфейс репозитория подборок
 */
public interface CompilationRepository extends CrudRepository<Compilation, Long> {
    /**
     * запрос списка всех подборок с учётом закрепления
     *
     * @param pinned   - закрепление
     * @param pageable - объект для создания запросов на перелистывание страниц
     * @return - список всех подборок
     */
    List<Compilation> findAllByPinned(boolean pinned, Pageable pageable);

    /**
     * запрос списка всех подборок
     *
     * @param pageable - объект для создания запросов на перелистывание страниц
     * @return - список всех подборок
     */
    List<Compilation> findAll(Pageable pageable);
}
