package ru.yandex.practicum.service.controllers.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.client.StatClient;
import ru.yandex.practicum.service.model.*;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс публичного контроллера событий
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventPublicControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @MockBean
    private StatClient statClient;
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
    private Category category = Category.builder()
            .name("test")
            .build();
    Event event3 = Event.builder()
            .title("test2")
            .description("description")
            .initiator(user)
            .annotation("test2")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(3))
            .paid(true)
            .participantLimit(0)
            .lon(0.0F)
            .lat(0.0F)
            .requestModeration(false)
            .category(category)
            .state(EventState.PUBLISHED)
            .build();
    private Category category1 = Category.builder()
            .name("test1")
            .build();
    Event event1 = Event.builder()
            .title("test1")
            .description("test1")
            .initiator(user1)
            .annotation("annotation")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(8))
            .paid(false)
            .lon(0.0F)
            .lat(0.0F)
            .participantLimit(1)
            .requestModeration(false)
            .category(category1)
            .state(EventState.PUBLISHED)
            .build();
    private Category category2 = Category.builder()
            .name("test2")
            .build();
    Event event2 = Event.builder()
            .title("test2")
            .description("test2")
            .initiator(user1)
            .annotation("test2")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(3))
            .paid(true)
            .lon(0.0F)
            .lat(0.0F)
            .participantLimit(0)
            .requestModeration(false)
            .category(category2)
            .state(EventState.CANCELED)
            .build();
    private Event event = Event.builder()
            .title("test")
            .description("test")
            .initiator(user)
            .annotation("test")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusHours(12))
            .paid(false)
            .lon(0.0F)
            .lat(0.0F)
            .participantLimit(0)
            .requestModeration(false)
            .category(category)
            .state(EventState.PENDING)
            .build();

    /**
     * параметризованный запрос списка событий
     *
     * @param argument - объект класса для передачи параметров запроса в тест
     */
    @ParameterizedTest
    @ArgumentsSource(FindArgumentsProvider.class)
    @Transactional
    void test38_findAll(FindPublicArgument argument) throws Exception {
        this.mockMvc.perform(get("/events?sort=FALSE"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Данный вариант сортировки не поддерживается"), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = ViewStats.builder()
                .uri("test1")
                .app("service")
                .hits(1L)
                .build();
        views.add(viewStat1);
        Mockito
                .when(statClient.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(mapper.writeValueAsString(views), HttpStatus.OK));
        StringBuilder sb = new StringBuilder();
        sb.append("/events");
        if (argument.getCategories() != null) {
            sb.append("?categories=");
            sb.append(argument.getCategories().get(0));
            argument.getCategories().stream()
                    .skip(1)
                    .forEach(c -> sb.append(",").append(c));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
        if (argument.getPaid() != null) {
            sb.append("?paid=");
            sb.append(argument.getPaid());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(4L), Long.class));
        }
        if (argument.getRangeStart() != null) {
            sb.append("?rangeStart=");
            sb.append(argument.getRangeStart());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class));
        }
        if (argument.getRangeEnd() != null) {
            sb.append("?rangeEnd=");
            sb.append(argument.getRangeEnd());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(4L), Long.class));
        }
        if (argument.getText() != null) {
            sb.append("?text=");
            sb.append(argument.getText());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(4L), Long.class));
        }
        if (argument.getSortMethod() != null) {
            sb.append("?sort=");
            sb.append(argument.getSortMethod());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(4L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
        if (argument.getOnlyAvailable() != null) {
            this.mockMvc.perform(post("/users/1/requests?eventId=2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.event", is(event1.getId()), Long.class))
                    .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                    .andExpect(jsonPath("$.requester", is(user.getId()), Long.class));
            sb.append("?onlyAvailable=");
            sb.append(argument.getOnlyAvailable());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(4L), Long.class));
        }
    }

    /**
     * поиск события по id
     */
    @Test
    @Transactional
    void test39_findById() throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = ViewStats.builder()
                .uri("test1")
                .app("service")
                .hits(1L)
                .build();
        views.add(viewStat1);
        Mockito
                .when(statClient.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(mapper.writeValueAsString(views), HttpStatus.OK));
        this.mockMvc.perform(get("/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(event1.getId()), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(event1.getInitiator().getId()), Long.class))
                .andExpect(jsonPath("$.state", is("PUBLISHED"), String.class))
                .andExpect(jsonPath("$.title", is(event1.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(event1.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(event1.getDescription()), String.class));
        this.mockMvc.perform(get("/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(get("/events/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * создание окружения
     */
    @BeforeEach
    private void createEnvironment() {
        category = categoryRepository.save(category);
        category1 = categoryRepository.save(category1);
        category2 = categoryRepository.save(category2);
        user = userRepository.save(user);
        user1 = userRepository.save(user1);
        event = eventRepository.save(event);
        event1 = eventRepository.save(event1);
        event2 = eventRepository.save(event2);
        event3 = eventRepository.save(event3);
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

    /**
     * класс для создания объектов параметров запроса для теста запроса событий
     */
    static class FindArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(FindPublicArgument.builder().text("TeSt").build()),
                    Arguments.of(FindPublicArgument.builder().paid(true).build()),
                    Arguments.of(FindPublicArgument.builder().categories(Arrays.asList(1L, 2L)).build()),
                    Arguments.of(FindPublicArgument.builder().rangeStart(LocalDateTime.now().plusDays(4)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(FindPublicArgument.builder().rangeEnd(LocalDateTime.now().plusDays(4)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(FindPublicArgument.builder().onlyAvailable(true).build()),
                    Arguments.of(FindPublicArgument.builder().sortMethod(SortMethod.EVENT_DATE).build())
            );
        }
    }
}