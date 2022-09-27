package ru.yandex.practicum.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.statistic.model.EndpointHitDto;

@RestController
@RequestMapping("/hit")
public class StatController {
    private StatService service;

    @Autowired
    public StatController(StatService service) {
        this.service = service;
    }
    @PostMapping
    public void createHit(@RequestBody EndpointHitDto dto) {
        service.create(dto);
    }
}
