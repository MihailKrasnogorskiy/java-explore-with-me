package ru.yandex.practicum.service.controllers.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.model.Compilation;
import ru.yandex.practicum.service.repositories.CompilationRepository;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс публичного контроллера подборок
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompilationPublicControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CompilationRepository compilationRepository;

    private Compilation compilation1 = Compilation.builder()
            .id(1)
            .events(new HashSet<>())
            .title("compilation1")
            .pinned(true)
            .build();
    private Compilation compilation2 = Compilation.builder()
            .id(2)
            .events(new HashSet<>())
            .title("compilation2")
            .pinned(false)
            .build();
    private Compilation compilation3 = Compilation.builder()
            .id(3)
            .events(new HashSet<>())
            .title("compilation3")
            .pinned(true)
            .build();

    @Test
    @Transactional
    void test23_findAll() throws Exception {
        this.mockMvc.perform(get("/compilations?from=0&size=10&pinned=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(compilation1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(compilation3.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilation1.getTitle()), String.class))
                .andExpect(jsonPath("$[1].title", is(compilation3.getTitle()), String.class));
        this.mockMvc.perform(get("/compilations?from=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(compilation1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(compilation2.getId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(compilation3.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilation1.getTitle()), String.class))
                .andExpect(jsonPath("$[1].title", is(compilation2.getTitle()), String.class))
                .andExpect(jsonPath("$[2].title", is(compilation3.getTitle()), String.class));
        this.mockMvc.perform(get("/compilations?from=0&size=1&pinned=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(compilation1.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilation1.getTitle()), String.class));
        this.mockMvc.perform(get("/compilations?from=0&size=10&pinned=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(compilation2.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilation2.getTitle()), String.class));
        this.mockMvc.perform(get("/compilations?from=1&size=10&pinned=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(compilation3.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilation3.getTitle()), String.class));
        this.mockMvc.perform(get("/compilations?from=0&size=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/compilations?from=-1&size=4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
    }

    @Test
    @Transactional
    void test24_findById() throws Exception {
        this.mockMvc.perform(get("/compilations/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(compilation1.getTitle()), String.class))
                .andExpect(jsonPath("$.id", is(compilation1.getId()), Long.class));
        this.mockMvc.perform(get("/compilations/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    @BeforeEach
    private void createEnvironment() {
        compilationRepository.save(compilation1);
        compilationRepository.save(compilation2);
        compilationRepository.save(compilation3);
    }

    @BeforeEach
    @AfterAll
    void clearEnvironment() {
        String query = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "ALTER TABLE compilations ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}