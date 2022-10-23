package ru.yandex.practicum.service.controllers.compilation;

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
import ru.yandex.practicum.service.model.Category;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.NewCompilationDto;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера подборок для администраторов
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompilationAdminControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CompilationRepository compilationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    private NewCompilationDto newDto = NewCompilationDto.builder()
            .events(new ArrayList<>())
            .title("title1")
            .build();
    private User user = User.builder()
            .name("vova")
            .email("mail@mail.ru")
            .build();
    private Category category = Category.builder()
            .name("test")
            .build();
    private Event event = Event.builder()
            .title("test")
            .description("test")
            .initiator(user)
            .annotation("test")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(10))
            .paid(false)
            .participantLimit(0)
            .requestModeration(false)
            .category(category)
            .state(EventState.PUBLISHED)
            .build();

    /**
     * создание подборки
     */
    @Test
    @Transactional
    void test17_create() throws Exception {
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(false), Boolean.class));
        newDto.setTitle("");
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        newDto.setTitle(null);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        newDto.setTitle("title1");
        newDto.getEvents().add(10L);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        newDto.getEvents().clear();
        newDto.setEvents(null);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.reason", is("Внутренняя ошибка сервера"), String.class));
        newDto.setEvents(new ArrayList<>());
    }

    /**
     * удаление подборки
     */
    @Test
    @Transactional
    void test18_delete() throws Exception {
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(false), Boolean.class));
        this.mockMvc.perform(delete("/admin/compilations/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isOk());
    }

    /**
     * удаление закрепления подборки
     */
    @Test
    @Transactional
    void test19_deletePin() throws Exception {
        newDto.setPinned(true);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(true), Boolean.class));
        this.mockMvc.perform(delete("/admin/compilations/2/pin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/0/pin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/1/pin"))
                .andExpect(status().isOk());
        assertFalse(compilationRepository.findById(1L).get().isPinned());
    }

    /**
     * закрепление подборки
     */
    @Test
    @Transactional
    void test20_pinned() throws Exception {
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(false), Boolean.class));
        this.mockMvc.perform(patch("/admin/compilations/2/pin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/0/pin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/1/pin"))
                .andExpect(status().isOk());
        assertTrue(compilationRepository.findById(1L).get().isPinned());
    }

    /**
     * удаление события из подборки
     */
    @Test
    @Transactional
    void test21_deleteEvent() throws Exception {
        userRepository.save(user);
        categoryRepository.save(category);
        event = eventRepository.save(event);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(false), Boolean.class));
        this.mockMvc.perform(delete("/admin/compilations/2/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/1/events/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/0/events/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(delete("/admin/compilations/1/events/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/1/events/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/admin/compilations/1/events/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/admin/compilations/1/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * добавление события в подборку
     */
    @Test
    @Transactional
    void test22_addEvent() throws Exception {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);
        this.mockMvc.perform(post("/admin/compilations").content(mapper.writeValueAsString(newDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(newDto.getTitle()), String.class))
                .andExpect(jsonPath("$.pinned", is(false), Boolean.class));
        this.mockMvc.perform(patch("/admin/compilations/2/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/1/events/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/0/events/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/1/events/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/admin/compilations/1/events/1"))
                .andExpect(status().isOk());
        assertTrue(compilationRepository.findById(1L).get().getEvents().contains(eventRepository.findById(1L).get()));
        this.mockMvc.perform(patch("/admin/compilations/1/events/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Повторное добавление событий запрещено"), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
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