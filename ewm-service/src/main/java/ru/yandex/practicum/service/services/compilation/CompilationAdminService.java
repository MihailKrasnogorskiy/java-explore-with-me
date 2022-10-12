package ru.yandex.practicum.service.services.compilation;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;

/**
 * интерфейс сервиса контроллера подборок для администраторов
 */
public interface CompilationAdminService {

    /**
     * создание подборки
     *
     * @param dto - dto объект для создания подборки
     * @return - dto объект подборки
     */
    @Transactional
    CompilationDto create(NewCompilationDto dto);

    /**
     * удаление подборки
     *
     * @param compId - id подборки
     */
    void delete(long compId);

    /**
     * открепление подборки
     *
     * @param compId - id подборки
     */
    void deletePin(long compId);

    /**
     * закрепление подборки
     *
     * @param compId - id подборки
     */
    void pinned(long compId);

    /**
     * удаление события из подборки
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    void deleteEvent(long compId, long eventId);

    /**
     * добавление события в подборку
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    void addEvent(long compId, long eventId);


}
