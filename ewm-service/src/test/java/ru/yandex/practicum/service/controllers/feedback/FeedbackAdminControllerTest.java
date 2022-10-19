package ru.yandex.practicum.service.controllers.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.service.model.FeedbackPost;
import ru.yandex.practicum.service.repositories.FeedbackRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера обратной связи для администраторов
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackAdminControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FeedbackRepository repository;

    private FeedbackPost post1 = FeedbackPost.builder()
            .userName("user1")
            .created(LocalDateTime.now())
            .post("test")
            .isPublish(true)
            .build();
    private FeedbackPost post2 = FeedbackPost.builder()
            .userName("user2")
            .created(LocalDateTime.now())
            .post("test2")
            .isPublish(true)
            .build();
    private FeedbackPost post3 = FeedbackPost.builder()
            .userName("user3")
            .created(LocalDateTime.now().minusDays(2))
            .post("test3")
            .isPublish(false)
            .build();

    @Test
    @Transactional
    void test11_findAllFeedback() throws Exception {
        this.mockMvc.perform(get("/admin/feedback?days=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].userName", is(post1.getUserName()), String.class))
                .andExpect(jsonPath("$[0].post", is(post1.getPost()), String.class))
                .andExpect(jsonPath("$[0].created", is(post1.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].userName", is(post2.getUserName()), String.class))
                .andExpect(jsonPath("$[1].post", is(post2.getPost()), String.class))
                .andExpect(jsonPath("$[1].created", is(post2.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class))
                .andExpect(jsonPath("$[2].id", is(3L), Long.class))
                .andExpect(jsonPath("$[2].userName", is(post3.getUserName()), String.class))
                .andExpect(jsonPath("$[2].post", is(post3.getPost()), String.class))
                .andExpect(jsonPath("$[2].created", is(post3.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class));
        this.mockMvc.perform(get("/admin/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].userName", is(post1.getUserName()), String.class))
                .andExpect(jsonPath("$[0].post", is(post1.getPost()), String.class))
                .andExpect(jsonPath("$[0].created", is(post1.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].userName", is(post2.getUserName()), String.class))
                .andExpect(jsonPath("$[1].post", is(post2.getPost()), String.class))
                .andExpect(jsonPath("$[1].created", is(post2.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class));
        this.mockMvc.perform(get("/admin/feedback?days=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
    }

    @Test
    @Transactional
    void test12_publishFeedbackPost() throws Exception {
        this.mockMvc.perform(patch("/admin/feedback/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.userName", is(post3.getUserName()), String.class))
                .andExpect(jsonPath("$.post", is(post3.getPost()), String.class))
                .andExpect(jsonPath("$.publish", is(true), Boolean.class))
                .andExpect(jsonPath("$.created", is(post3.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), String.class));
        this.mockMvc.perform(patch("/admin/feedback/4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/admin/feedback/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
    }

    @Test
    @Transactional
    void test13_deleteFeedbackPost() throws Exception {
        assertTrue(repository.existsById(3L));
        this.mockMvc.perform(delete("/admin/feedback/3"))
                .andExpect(status().isOk());
        assertFalse(repository.existsById(3L));
        this.mockMvc.perform(patch("/admin/feedback/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/admin/feedback/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));

    }

    @BeforeEach
    void createEnvironment() {
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
    }

    /**
     * очистка окружения и сброс счётчика в таблице
     */
    @BeforeEach
    void clearEnvironment() {
        String query = "ALTER TABLE feedback ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }
}