package ru.yandex.practicum.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.service.model.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

/**
 * класс http клиента для обращения к сервису статистики
 */

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(@Value("${statistics.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createHit(EndpointHitDto hit) {
        return post("/hit", hit);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb = new StringBuilder();
        if (!uris.isEmpty()) {
            sb.append("/events/");
            sb.append(uris.get(0));
            uris.stream()
                    .skip(1)
                    .forEach(u -> sb.append(",/events/").append(u));

        }

        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.valueOf(sb),
                "unique", unique.toString()
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

}