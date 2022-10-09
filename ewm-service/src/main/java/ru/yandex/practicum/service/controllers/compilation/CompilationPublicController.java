package ru.yandex.practicum.service.controllers.compilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.model.dto.CompilationDto;
import ru.yandex.practicum.service.services.compilation.CompilationPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * публичный контроллер подборок
 */
@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationPublicService service;

    @Autowired
    public CompilationPublicController(CompilationPublicService service) {
        this.service = service;
    }

    /**
     * получение списка всех подборок
     *
     * @param from - количество подборок, которые нужно пропустить для формирования текущего набора
     * @param size - количество подборок в наборе
     * @return список всех подборок
     */
    @GetMapping()
    public List<CompilationDto> findAll(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(required = false) Boolean pinned) {
        return service.findAll(pinned, from, size);
    }

    /**
     * получение информации о подборке по id
     *
     * @param compId - id подборки
     * @return - dto объект подборки
     */
    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable Long compId) {
        return service.findById(compId);
    }
}
