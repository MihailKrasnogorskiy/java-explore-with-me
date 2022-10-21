package ru.yandex.practicum.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.service.exceptions.HttpClientException;
import ru.yandex.practicum.service.model.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

/**
 * класс http клиента для обращения к сервису статистики
 */

@Service
public class StatClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private String baseUrl;

    @Autowired
    public StatClient(@Value("${statistics.url}") String baseUrl, RestTemplateBuilder builder,
                      ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
    }


    public void createHit(EndpointHitDto hit) {
        String postPath = "/hit";
        ResponseEntity<String> response = restTemplate.postForEntity(postPath, hit, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new HttpClientException(String.format("Stats server response code is %d, error: %s",
                    response.getStatusCode().value(), response.getBody()));
        }
    }

    public ResponseEntity<String> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb = new StringBuilder();
        sb.append("/stats?start=")
                .append(start)
                .append("&end=")
                .append(end)
                .append("&uris=");
        if (!uris.isEmpty()) {
            sb.append("/events/");
            sb.append(uris.get(0));
            uris.stream()
                    .skip(1)
                    .forEach(u -> sb.append(",/events/").append(u));
        }
        sb.append("&unique=")
                .append(unique);
        String getPath = sb.toString();
        return restTemplate.getForEntity(getPath, String.class);
    }
}