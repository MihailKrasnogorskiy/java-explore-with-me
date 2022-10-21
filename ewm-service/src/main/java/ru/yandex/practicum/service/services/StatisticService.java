package ru.yandex.practicum.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
        ResponseEntity<String> response = client.getStats(codeStart, codeEnd, eventIds, false);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(response.getBody());
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            String body = response.getBody();

            List<ViewStats> stats = new ArrayList<>();


            try {
                stats.addAll(mapper.readValue(body, new TypeReference<List<ViewStats>>() {
                }));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }


            if (!stats.isEmpty()) {
                for (int i = 0; i < stats.size(); i++) {
                    events.get(i).setViews(stats.get(i).getHits());
                }
            }
        }
        return events;
    }
}


