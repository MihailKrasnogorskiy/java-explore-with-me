package ru.yandex.practicum.service.controllers.feedback;

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
import ru.yandex.practicum.service.model.FeedbackPost;
import ru.yandex.practicum.service.model.dto.FeedbackPostCreateDto;
import ru.yandex.practicum.service.repositories.FeedbackRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс публичного контроллера обратной связи
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackPublicControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FeedbackRepository repository;

    private FeedbackPostCreateDto createDto = new FeedbackPostCreateDto("user", "test");

    /**
     * создание сообщения обратной связи
     */
    @Test
    @Transactional
    void test9_createFeedbackPost() throws Exception {
        this.mockMvc.perform(post("/feedback").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(repository.existsById(1L));
        assertEquals(createDto.getUserName(), repository.findById(1L).get().getUserName());
        assertEquals(createDto.getPost(), repository.findById(1L).get().getPost());
        createDto.setUserName("");
        this.mockMvc.perform(post("/feedback").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        createDto.setUserName(null);
        this.mockMvc.perform(post("/feedback").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        createDto.setUserName("user");
        createDto.setPost("");
        this.mockMvc.perform(post("/feedback").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        createDto.setPost(null);
        this.mockMvc.perform(post("/feedback").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
    }

    /**
     * получение списка сообщения обратной связи
     */
    @Test
    @Transactional
    void test10_findAllFeedback() throws Exception {
        FeedbackPost post1 = FeedbackPost.builder()
                .userName("user1")
                .created(LocalDateTime.now())
                .post("test")
                .isPublish(true)
                .build();
        FeedbackPost post2 = FeedbackPost.builder()
                .userName("user2")
                .created(LocalDateTime.now())
                .post("test2")
                .isPublish(true)
                .build();
        FeedbackPost post3 = FeedbackPost.builder()
                .userName("user3")
                .created(LocalDateTime.now())
                .post("test3")
                .isPublish(false)
                .build();
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        this.mockMvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName", is(post1.getUserName()), String.class))
                .andExpect(jsonPath("$[0].post", is(post1.getPost()), String.class))
                .andExpect(jsonPath("$[0].created", is(post1.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class))
                .andExpect(jsonPath("$[1].userName", is(post2.getUserName()), String.class))
                .andExpect(jsonPath("$[1].post", is(post2.getPost()), String.class))
                .andExpect(jsonPath("$[1].created", is(post2.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class));
    }

    /**
     * очистка окружения и сброс счётчика в таблице
     */
    @BeforeEach
    @AfterAll
    void clearEnvironment() {
        String query = "ALTER TABLE feedback ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}