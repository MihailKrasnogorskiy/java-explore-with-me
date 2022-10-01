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

@RestController
public class StatController {
    private StatService service;

    @Autowired
    public StatController(StatService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public void createHit(@RequestBody EndpointHitDto dto) {
        service.create(dto);
    }

    @GetMapping("/stats")
    public List<ViewStats> findStats(@RequestParam String start, @RequestParam String end,
                                     @RequestParam(defaultValue = "false") Boolean unique,
                                     @RequestBody List<String> uris) {
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            String decodeStart = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());
            String decodeEnd = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());
            startTime = LocalDateTime.parse(decodeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDateTime.parse(decodeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
        return service.findStats(startTime, endTime, uris, unique);
    }
}
