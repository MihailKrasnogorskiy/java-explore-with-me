package ru.yandex.practicum.service.controllers.request;

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
import ru.yandex.practicum.service.model.Category;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера запросов на участие для пользователей
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestUserControllerTest {
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
    private User user = User.builder()
            .name("vova")
            .email("mail@mail.ru")
            .build();
    private User user1 = User.builder()
            .name("vova")
            .email("gmail@mail.ru")
            .build();
    private User user2 = User.builder()
            .name("vova")
            .email("vmail@mail.ru")
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
    private Event event1 = Event.builder()
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
            .state(EventState.PENDING)
            .build();

    @Test
    @Transactional
    void test14_create() throws Exception {
        createEnvironment();
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(event.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Дублирование запросов запрещено."), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(post("/users/1/requests?eventId=1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(post("/users/2/requests?eventId=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(post("/users/0/requests?eventId=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(post("/users/10/requests?eventId=1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(post("/users/2/requests?eventId=5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(post("/users/2/requests?eventId=2"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("На участие в неопубликованных событиях заявки не принимаются"),
                        String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        event1.setState(EventState.PUBLISHED);
        event1.setParticipantLimit(1);
        event1.setRequestModeration(true);
        eventRepository.save(event1);
        this.mockMvc.perform(post("/users/3/requests?eventId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.event", is(event1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("PENDING"), String.class))
                .andExpect(jsonPath("$.requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(post("/users/3/requests?eventId=2"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Дублирование запросов запрещено."), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(patch("/users/1/events/2/requests/2/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.event", is(event1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(post("/users/2/requests?eventId=2"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("На событие с id = '2' достигнут лимит участников"), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
    }

    @Test
    @Transactional
    void test15_cancel() throws Exception {
        createEnvironment();
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(event.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(patch("/users/2/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(event.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("CANCELED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(patch("/users/5/requests/1/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/users/2/requests/2/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/users/0/requests/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/users/2/requests/0/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
    }

    @Test
    @Transactional
    void test16_findAllByRequesterId() throws Exception {
        createEnvironment();
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(event.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(get("/users/2/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].event", is(event.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$[0].requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        this.mockMvc.perform(get("/users/0/requests"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/users/10/requests"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));

    }

    private void createEnvironment() {
        category = categoryRepository.save(category);
        user = userRepository.save(user);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        event = eventRepository.save(event);
        event1 = eventRepository.save(event1);
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
        query = "ALTER TABLE requests ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}