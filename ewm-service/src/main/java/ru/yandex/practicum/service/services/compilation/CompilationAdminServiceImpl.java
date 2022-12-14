package ru.yandex.practicum.service.services.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.exceptions.RepeatedAddEventException;
import ru.yandex.practicum.service.model.Compilation;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;
import ru.yandex.practicum.service.model.mappers.CompilationMapper;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;

import java.util.Set;

/**
 * сервис контроллера подборок для администраторов
 */
@Service
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Autowired
    public CompilationAdminServiceImpl(CompilationMapper compilationMapper, EventRepository eventRepository,
                                       CompilationRepository compilationRepository) {
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.compilationRepository = compilationRepository;
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        dto.getEvents().forEach(this::validateEventId);
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(dto));
        log.info("Подборка с id = {} создана", compilation.getId());
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        validateCompilationId(compId);
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {} удалена", compId);
    }

    @Override
    @Transactional
    public void deletePin(long compId) {
        validateCompilationId(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Подборка с id = {} откреплена", compId);
    }

    @Override
    @Transactional
    public void pinned(long compId) {
        validateCompilationId(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("Подборка с id = {} закреплена", compId);
    }

    @Override
    @Transactional
    public void deleteEvent(long compId, long eventId) {
        validateEventId(eventId);
        validateCompilationId(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if (events.contains(eventRepository.findById(eventId).get())) {
            events.remove(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
            log.info("Из подборки с id = {} удалено событие с id = {}", compId, eventId);
        } else {
            throw new NotFoundException(String.format("В подборке id = '%s' событие id = '%s' не найдено", compId,
                    eventId));
        }
    }

    @Override
    @Transactional
    public void addEvent(long compId, long eventId) {
        validateEventId(eventId);
        validateCompilationId(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if (!events.contains(eventRepository.findById(eventId).get())) {
            events.add(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
            log.info("В подборку с id = {} добавлено событие с id = {}", compId, eventId);
        } else {
            throw new RepeatedAddEventException();
        }
    }

    /**
     * проверка на существование события по id
     *
     * @param eventId - id события
     */
    private void validateEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Событие с id = '%s' не найдено", eventId));
        }
    }

    /**
     * проверка на существование подборки по id
     *
     * @param compId - id события
     */
    private void validateCompilationId(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Подборка с id = '%s' не найдена", compId));
        }
    }
}
