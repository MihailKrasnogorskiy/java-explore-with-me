package ru.yandex.practicum.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.EndpointHitMapper;

@Service
public class StatServiceImpl implements StatService {
    private StatRepository repository;

    @Autowired
    public StatServiceImpl(StatRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void create(EndpointHitDto dto) {
        repository.save(EndpointHitMapper.toEndpointHit(dto));
    }
}
