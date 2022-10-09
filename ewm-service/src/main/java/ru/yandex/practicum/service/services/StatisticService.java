package ru.yandex.practicum.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.client.StatClient;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.ViewStats;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * сервис получения статистики
 */
@Service
@Slf4j
public class StatisticService {
    private final StatClient client;
    private final ObjectMapper mapper;

    @Autowired
    public StatisticService(StatClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * получение статистики просмотров
     *
     * @param events - список событий
     * @return - список событий с заполненными просмотрами
     */
    public List<Event> getStatistic(List<Event> events) {
        LocalDateTime startStat = LocalDateTime.now().minusDays(365);
        LocalDateTime endStat = LocalDateTime.now().plusDays(1);
        String codeStart;
        String codeEnd;
        try {
            String start = startStat.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String end = endStat.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            codeStart = URLEncoder.encode(start, StandardCharsets.UTF_8.toString());
            codeEnd = URLEncoder.encode(end, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
        List<String> eventIds = events.stream()
                .map(Event::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        ResponseEntity<Object> response = client.getStats(codeStart, codeEnd, eventIds, false);
        List<ViewStats> stats = new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful()) {

            List<Map<String, Object>> body = (List<Map<String, Object>>) response.getBody();
            if (body != null && body.size() > 0) {
                for (Map<String, Object> s : body) {
                    ViewStats viewStats = ViewStats.builder()
                            .app(s.get("app").toString())
                            .uri(s.get("uri").toString())
                            .hits(((Number) s.get("hits")).longValue())
                            .build();
                    stats.add(viewStats);
                }
            }
        }
        if (!stats.isEmpty()) {
            for (int i = 0; i < stats.size(); i++) {
                events.get(i).setViews(stats.get(i).getHits());
            }
        }
        return events;
    }

}
