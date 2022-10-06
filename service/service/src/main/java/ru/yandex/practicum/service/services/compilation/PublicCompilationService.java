package ru.yandex.practicum.service.services.compilation;

import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.CompilationDto;

import java.util.List;

/**
 * интерфейс публичного сервиса подборок
 */
public interface PublicCompilationService {
    /**
     * получение списка всех подборок
     *
     * @param from - количество подборок, которые нужно пропустить для формирования текущего набора
     * @param size - количество подборок в наборе
     * @return список всех подборок
     */
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    /**
     * получение информации о подборке по id
     *
     * @param compId - id подборки
     * @return - dto объект подборки
     */
    CompilationDto findById(Long compId);
}
