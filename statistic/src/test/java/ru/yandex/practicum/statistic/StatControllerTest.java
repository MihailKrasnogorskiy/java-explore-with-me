package ru.yandex.practicum.statistic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.statistic.model.EndpointHit;
import ru.yandex.practicum.statistic.model.EndpointHitDto;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера статистики
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class StatControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private StatRepository repository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final EndpointHitDto dto = EndpointHitDto.builder()
            .app("service")
            .ip("172.23.58.14")
            .uri("yandex.ru")
            .timestamp("2022-10-02 12:00:54")
            .build();

    private final EndpointHitDto dto1 = EndpointHitDto.builder()
            .app("service")
            .ip("172.23.58.15")
            .uri("yandex.ru")
            .timestamp("2022-10-02 12:00:54")
            .build();

    private final EndpointHitDto dto2 = EndpointHitDto.builder()
            .app("service")
            .ip("172.23.58.14")
            .uri("ya.ru")
            .timestamp("2022-10-02 12:00:54")
            .build();

    private final EndpointHitDto dto3 = EndpointHitDto.builder()
            .app("service")
            .ip("172.23.58.15")
            .uri("ya.ru")
            .timestamp("2022-10-02 12:00:54")
            .build();

    /**
     * создание записи о просмотре
     */
    @Test
    @Transactional
    void test01_createHit() throws Exception {
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<EndpointHit> list = (List<EndpointHit>) repository.findAll();
        assertEquals(1, list.get(0).getId());
        assertEquals(dto.getApp(), list.get(0).getApp());
        assertEquals(dto.getIp(), list.get(0).getIp());
    }

    /**
     * получение статистики
     */
    @Test
    @Transactional
    void test02_findStats() throws Exception {
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto1.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto1.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto2.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto2.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto3.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto3.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String start = "2022-09-29 12:00:00";
        String end = "2022-10-30 12:00:00";
        String start1 = "2022-09-30 13:00:00";
        try {
            start = URLEncoder.encode(start, StandardCharsets.UTF_8.toString());
            end = URLEncoder.encode(end, StandardCharsets.UTF_8.toString());
            start1 = URLEncoder.encode(start1, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
        String uri1 = "yandex.ru";
        String uri2 = "ya.ru";
        String path = "/stats?start=" + start + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=false";
        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(6L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(6L), Long.class));

        path = "/stats?start=" + start1 + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=false";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(4L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(4L), Long.class));

        path = "/stats?start=" + start + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=true";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(3L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(3L), Long.class));

        path = "/stats?start=" + start1 + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=true";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(2L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(2L), Long.class));

        clearEnvironment();
    }

    /**
     * очистка окружения и сброс счётчика в таблице
     */
    @BeforeEach
    void clearEnvironment() {
        String query = "ALTER TABLE hits ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "TRUNCATE TABLE hits ";
        jdbcTemplate.update(query);
    }
}