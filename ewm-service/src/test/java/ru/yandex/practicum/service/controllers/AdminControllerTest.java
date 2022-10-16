package ru.yandex.practicum.service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.NewUserRequest;
import ru.yandex.practicum.service.repositoryes.UserRepository;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера администраторов
 */

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final NewUserRequest userDto = new NewUserRequest("vova", "vova@mail.ru");

    /**
     * создание пользователя
     */
    @Test
    @Transactional
    void test01_createUser() throws Exception {
        this.mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<User> list = (List<User>) userRepository.findAll();
        assertEquals(1, list.get(0).getId());
        assertEquals(userDto.getName(), list.get(0).getName());
        assertEquals(userDto.getEmail(), list.get(0).getEmail());
    }

    /**
     * удаление пользователя
     */

    @Test
    @Transactional
    void test05_deleteUser() throws Exception {
        this.mockMvc.perform(post("/admin/users").content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk());
        List<User> list = (List<User>) userRepository.findAll();
        assertTrue(list.isEmpty());
    }

    @Test
    @Transactional
    void test06_findUsers() throws Exception {
        this.mockMvc.perform(post("/admin/users").content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userDto.setEmail("vova1@mail.ru");
        this.mockMvc.perform(post("/admin/users").content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/admin/users?ids=1&ids=2&from=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$[1].name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$[1].email", is(userDto.getEmail()), String.class));
    }

    /**
     * очистка окружения и сброс счётчика в таблице
     */
    @BeforeEach
    void clearEnvironment() {
        String query = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
    }


}