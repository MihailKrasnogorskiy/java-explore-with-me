package ru.yandex.practicum.service.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * класс для хранилища событий в подборках
 */
@Component
public class CompilationEventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CompilationEventStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * удаление события из подборки
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    public void deleteEvent(long compId, long eventId) {
        String sqlQuery = "DELETE FROM compilation_events WHERE compilation_id = ? and event_id = ?";
        jdbcTemplate.update(sqlQuery, compId, eventId);
    }

    /**
     * добавление события в подборку
     *
     * @param compId  - id подборки
     * @param eventId - id события
     */
    public void addEvent(long compId, long eventId) {
        String sqlQuery = "INSERT INTO compilation_events (compilation_id, event_id) VALUES ( ?,? )";
        jdbcTemplate.update(sqlQuery, compId, eventId);
    }
}
