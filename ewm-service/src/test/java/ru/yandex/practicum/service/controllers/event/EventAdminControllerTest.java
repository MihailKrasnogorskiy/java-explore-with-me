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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.client.StatClient;
import ru.yandex.practicum.service.model.*;
import ru.yandex.practicum.service.model.dto.AdminUpdateEventRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тестовый класс контроллера событий для пользователей
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventAdminControllerTest {
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
    private User user2 = User.builder()
            .name("vova")
            .email("vmail@mail.ru")
            .build();
    private Category category = Category.builder()
            .name("test")
            .build();
    private Category category1 = Category.builder()
            .name("test1")
            .build();
    Event event1 = Event.builder()
            .title("test1")
            .description("test1")
            .initiator(user1)
            .annotation("test1")
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(8))
            .paid(true)
            .participantLimit(0)
            .lon(0.0F)
            .lat(0.0F)
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
            .participantLimit(0)
            .lon(0.0F)
            .lat(0.0F)
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
     * публикация события
     */
    @Test
    @Transactional
    void test34_publish() throws Exception {
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
        this.mockMvc.perform(patch("/admin/events/1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PUBLISHED"), String.class))
                .andExpect(jsonPath("$.title", is(event.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(event.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(event.getDescription()), String.class));
        event.setEventDate(LocalDateTime.now().plusMinutes(30));
        event.setState(EventState.PENDING);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/admin/events/1/publish"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        event.setEventDate(LocalDateTime.now().plusDays(5));
        eventRepository.save(event);
        this.mockMvc.perform(patch("/admin/events/404/publish"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * отклонение события
     */
    @Test
    @Transactional
    void test35_reject() throws Exception {
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
        this.mockMvc.perform(patch("/admin/events/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("CANCELED"), String.class))
                .andExpect(jsonPath("$.title", is(event.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(event.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(event.getDescription()), String.class));
        event.setState(EventState.PUBLISHED);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/admin/events/1/reject"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        event.setState(EventState.PENDING);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/admin/events/404/reject"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    /**
     * обновление события
     *
     * @param updateDto - dto объект для обновления события
     */
    @ParameterizedTest
    @ArgumentsSource(UpdateArgumentsProvider.class)
    @Transactional
    void test36_update(AdminUpdateEventRequest updateDto) throws Exception {
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
        this.mockMvc.perform(put("/admin/events/404").content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        if (updateDto.getAnnotation() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.annotation", is(updateDto.getAnnotation()), String.class));
        }
        if (updateDto.getDescription() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.description", is(updateDto.getDescription()), String.class));
        }
        if (updateDto.getTitle() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.title", is(updateDto.getTitle()), String.class));
        }
        if (updateDto.getEventDate() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.eventDate", is(updateDto.getEventDate()), String.class));
        }
        if (updateDto.getPaid() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.paid", is(updateDto.getPaid()), Boolean.class));
        }
        if (updateDto.getParticipantLimit() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.participantLimit", is(updateDto.getParticipantLimit()), Integer.class));
        }
        if (updateDto.getCategory() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.category.id", is(updateDto.getCategory()), Long.class));
        }
        if (updateDto.getRequestModeration() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.requestModeration", is(updateDto.getRequestModeration()), Boolean.class));
        }
        if (updateDto.getAdminComment() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.state", is("REVISION"), String.class));
            this.mockMvc.perform(get("/users/1/events/revision"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[0].state", is("REVISION"), String.class))
                    .andExpect(jsonPath("$[0].adminComment", is(updateDto.getAdminComment()), String.class));
        }
    }

    /**
     * параметризованный запрос списка событий
     *
     * @param argument - объект класса для передачи параметров поиска в тест
     */
    @ParameterizedTest
    @ArgumentsSource(FindArgumentsProvider.class)
    @Transactional
    void test37_findAll(FindAdminArgument argument) throws Exception {
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
        sb.append("/admin/events");
        if (argument.getUsers() != null) {
            sb.append("?users=");
            sb.append(argument.getUsers().get(0));
            argument.getUsers().stream()
                    .skip(1)
                    .forEach(u -> sb.append(",").append(u));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[2].id", is(3L), Long.class));
        }
        if (argument.getStates() != null) {
            sb.append("?states=");
            sb.append(argument.getStates().get(0));
            argument.getStates().stream()
                    .skip(1)
                    .forEach(s -> sb.append(",").append(s));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
        if (argument.getCategories() != null) {
            sb.append("?categories=");
            sb.append(argument.getCategories().get(0));
            argument.getCategories().stream()
                    .skip(1)
                    .forEach(c -> sb.append(",").append(c));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
        if (argument.getRangeStart() != null) {
            sb.append("?rangeStart=");
            sb.append(argument.getRangeStart());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(3L), Long.class));
        }
        if (argument.getRangeEnd() != null) {
            sb.append("?rangeEnd=");
            sb.append(argument.getRangeEnd());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(3L), Long.class));
        }

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
        event.setEventDate(LocalDateTime.now().plusHours(12));
        event = eventRepository.save(event);
        event1 = eventRepository.save(event1);
        event2 = eventRepository.save(event2);
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
     * класс для создания параметров к тесту обновления события
     */
    static class UpdateArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(AdminUpdateEventRequest.builder().annotation("update").build()),
                    Arguments.of(AdminUpdateEventRequest.builder().description("update").build()),
                    Arguments.of(AdminUpdateEventRequest.builder().title("update").build()),
                    Arguments.of(AdminUpdateEventRequest.builder().
                            eventDate(LocalDateTime.now().plusDays(5)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(AdminUpdateEventRequest.builder().paid(true).build()),
                    Arguments.of(AdminUpdateEventRequest.builder().participantLimit(50).build()),
                    Arguments.of(AdminUpdateEventRequest.builder().category(2L).build()),
                    Arguments.of(AdminUpdateEventRequest.builder().requestModeration(true).build()),
                    Arguments.of(AdminUpdateEventRequest.builder().adminComment("admin comment").build())
            );
        }
    }

    /**
     * класс для создания параметров к тесту запроса списка событий
     */
    static class FindArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(FindAdminArgument.builder().users(Arrays.asList(1L, 2L)).build()),
                    Arguments.of(FindAdminArgument.builder().states(Arrays.asList(EventState.PENDING, EventState.PUBLISHED))
                            .build()),
                    Arguments.of(FindAdminArgument.builder().categories(Arrays.asList(1L, 2L)).build()),
                    Arguments.of(FindAdminArgument.builder().rangeStart(LocalDateTime.now().plusDays(1)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(FindAdminArgument.builder().rangeEnd(LocalDateTime.now().plusDays(4)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build())
            );
        }
    }
}