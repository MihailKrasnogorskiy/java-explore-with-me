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
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера категорий для администраторов
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryAdminControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    private NewCategoryDto createDto = new NewCategoryDto("test");

    /**
     * создание категории
     */
    @Test
    @Transactional
    void test4_createCategory() throws Exception {
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        createDto.setName("");
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        createDto.setName(null);
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
    }

    /**
     * обновление категории
     */
    @Test
    @Transactional
    void test5_updateCategory() throws Exception {
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("update")
                .build();
        this.mockMvc.perform(patch("/admin/categories").content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class));
        dto.setName("");
        this.mockMvc.perform(patch("/admin/categories").content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        dto.setName(null);
        this.mockMvc.perform(patch("/admin/categories").content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        dto.setName("test");
        dto.setId(0L);
        this.mockMvc.perform(patch("/admin/categories").content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * удаление категории
     */
    @Test
    @Transactional
    void test6_deleteCategory() throws Exception {
        createDto.setName("test");
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createDto.getName()), String.class))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
        this.mockMvc.perform(delete("/admin/categories/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/admin/categories/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        createDto.setName("test");
        this.mockMvc.perform(post("/admin/categories").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        User user = User.builder()
                .name("vova")
                .email("mail@mail.ru")
                .build();
        user = userRepository.save(user);
        Event event = Event.builder()
                .title("test")
                .description("test")
                .initiator(user)
                .annotation("test")
                .createdOn(LocalDateTime.now())
                .eventDate(LocalDateTime.now().plusDays(10))
                .paid(false)
                .participantLimit(0)
                .requestModeration(false)
                .category(categoryRepository.findById(2L).get())
                .build();
        eventRepository.save(event);
        this.mockMvc.perform(delete("/admin/categories/2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.reason", is("Запрос приводит к нарушению целостности данных"), String.class));
    }

    /**
     * очистка окружения и сброс счётчика в таблицах
     */
    @BeforeEach
    @AfterAll
    void clearEnvironment() {
        String query = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}