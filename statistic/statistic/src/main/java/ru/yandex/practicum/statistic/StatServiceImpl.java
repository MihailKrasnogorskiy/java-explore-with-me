package ru.yandex.practicum.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.EndpointHitMapper;
import ru.yandex.practicum.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ViewStats> findStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris,
                                     Boolean unique) {
        List<ViewStats> list = new ArrayList<>();
        if (unique) {
            uris.stream()
                    .map(uri -> ViewStats.builder()
                            .app("service")
                            .uri(uri)
                            .hits(repository.getViewUnique(startTime, endTime, uri))
                            .build())
                    .forEach(list::add);
        } else {
            uris.stream()
                    .map(uri -> ViewStats.builder()
                            .app("service")
                            .uri(uri)
                            .hits(repository.getView(startTime, endTime, uri))
                            .build())
                    .forEach(list::add);
        }
        return list;
    }
}
