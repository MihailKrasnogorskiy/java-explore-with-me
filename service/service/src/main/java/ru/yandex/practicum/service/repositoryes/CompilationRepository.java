package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.service.model.Compilation;

/**
 * интерфейс репозитория подборок
 */
public interface CompilationRepository extends CrudRepository<Compilation, Long> {
}
