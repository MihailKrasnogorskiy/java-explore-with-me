package ru.yandex.practicum.service.controllers.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс публичного контроллера категорий
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryPublicControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private NewCategoryDto createDto = new NewCategoryDto("test");

    /**
     * запрос списка всех категории
     */
    @Test
    @Transactional
    void test7_findAllCategories() throws Exception {
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        NewCategoryDto createDto1 = new NewCategoryDto("test1");
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto1.getName()), String.class))
                .andExpect(jsonPath("$.id", is(2L), Long.class));
        this.mockMvc.perform(get("/categories?from=-1&size=10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/categories?from=0&size=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[1].name", is(createDto1.getName()), String.class))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        this.mockMvc.perform(get("/categories?size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class));

    }

    /**
     * поиск категории по id
     */
    @Test
    @Transactional
    void test8_findCategoryById() throws Exception {
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        this.mockMvc.perform(get("/categories/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        this.mockMvc.perform(get("/categories/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * очистка окружения и сброс счётчика в таблице
     */
    @BeforeEach
    @AfterAll
    void clearEnvironment() {
        String query = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}