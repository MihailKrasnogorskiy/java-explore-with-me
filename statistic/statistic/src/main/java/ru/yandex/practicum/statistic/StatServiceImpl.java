package ru.yandex.practicum.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.statistic.model.EndpointHitDto;
import ru.yandex.practicum.statistic.model.EndpointHitMapper;
import ru.yandex.practicum.statistic.model.ViewStats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        log.info("Создана запись о просмотре uri {} c ip {}", dto.getUri(), dto.getIp());
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
            log.info("Статистика просмотров  uris {} учётом уникальности ip выгружена", uris.toString() );
        } else {
            uris.stream()
                    .map(uri -> ViewStats.builder()
                            .app("service")
                            .uri(uri)
                            .hits(repository.getView(startTime, endTime, uri))
                            .build())
                    .forEach(list::add);
            log.info("Статистика просмотров  uris {} без учёта уникальности ip выгружена", uris.toString() );
        }
        return list;
    }
}
