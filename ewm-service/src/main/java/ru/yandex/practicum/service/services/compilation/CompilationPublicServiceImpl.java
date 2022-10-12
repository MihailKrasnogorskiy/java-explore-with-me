package ru.yandex.practicum.service.services.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.OffsetLimitPageable;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.model.mappers.CompilationMapper;
import ru.yandex.practicum.service.repositoryes.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис публичного контроллера подборок
 */
@Service
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;

    public CompilationPublicServiceImpl(CompilationMapper compilationMapper,
                                        CompilationRepository compilationRepository) {
        this.compilationMapper = compilationMapper;
        this.compilationRepository = compilationRepository;
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto findById(Long compId) {
        validateCompilationId(compId);
        return compilationMapper.toCompilationDto(compilationRepository.findById(compId).get());
    }

    /**
     * проверка на существование подборки по id
     *
     * @param compId - id события
     */
    private void validateCompilationId(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка с id = " + compId + " не найдена");
        }
    }
}
