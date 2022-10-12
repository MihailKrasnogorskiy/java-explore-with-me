package ru.yandex.practicum.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.ViewStats;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * контроллер статистики
 */
@RestController
public class StatController {
    private final StatService service;

    @Autowired
    public StatController(StatService service) {
        this.service = service;
    }

    /**
     * сохранение просмотра
     *
     * @param dto - dto объект просмотра эндпонта
     */
    @PostMapping("/hit")
    public void createHit(@RequestBody EndpointHitDto dto) {
        service.create(dto);
    }

    /**
     * запрос статистики
     *
     * @param start  - дата и время начала диапазона за который нужно выгрузить статистик
     * @param end    - дата и время конца диапазона за который нужно выгрузить статистику
     * @param unique - нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @param uris   - список uri для которых нужно выгрузить статистику
     * @return - список объектов возвращаемой статистики
     */
    @GetMapping("/stats")
    public List<ViewStats> findStats(@RequestParam String start, @RequestParam String end,
                                     @RequestParam(defaultValue = "false") Boolean unique,
                                     @RequestParam List<String> uris) {
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            String decodeStart = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());
            String decodeEnd = URLDecoder.decode(end, StandardCharsets.UTF_8.toString());
            startTime = LocalDateTime.parse(decodeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDateTime.parse(decodeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
        return service.findStats(startTime, endTime, uris, unique);
    }
}
