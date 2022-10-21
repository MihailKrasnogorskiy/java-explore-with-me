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
import ru.yandex.practicum.service.model.dto.Location;
import ru.yandex.practicum.service.model.dto.NewEventDto;
import ru.yandex.practicum.service.model.dto.UpdateEventRequest;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
class EventUsersControllerTest {
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
    private NewEventDto createDto = NewEventDto.builder()
            .annotation("Тестовое событие")
            .category(1)
            .location(new Location(0.0F,0.0F))
            .description("Описание тестового события")
            .eventDate(LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .title("тест")
            .build();

    @Test
    @Transactional
    void test25_createEvent() throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        this.mockMvc.perform(post("/users/100/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(post("/users/0/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        createDto.setEventDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Событие не может быть создано, изменено или опубликовано менее" +
                        " чем за '2' час(а) до его начала"), String.class))
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        createDto.setEventDate(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "HH:mm:ss")));
    }

    @Test
    @Transactional
    void test26_getEventByIdWhereUserIsOwner() throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        this.mockMvc.perform(get("/users/1/events/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Запрос составлен с ошибкой"), String.class));
        this.mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        this.mockMvc.perform(get("/users/1/events/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(get("/users/2/events/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(get("/users/10/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    @Test
    @Transactional
    void test27_getAllRevision() throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        createDto.setTitle("тест2");
        this.mockMvc.perform(post("/users/2/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(2L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        createDto.setTitle("тест3");
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        createDto.setTitle("тест4");
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        Event event1 = eventRepository.findById(1L).get();
        Event event2 = eventRepository.findById(2L).get();
        Event event3 = eventRepository.findById(3L).get();
        event1.setState(EventState.REVISION);
        event2.setState(EventState.REVISION);
        event3.setState(EventState.REVISION);
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
        this.mockMvc.perform(get("/users/1/events/revision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].state", is("REVISION"), String.class))
                .andExpect(jsonPath("$[0].title", is("тест"), String.class))
                .andExpect(jsonPath("$[1].id", is(3L), Long.class))
                .andExpect(jsonPath("$[1].state", is("REVISION"), String.class))
                .andExpect(jsonPath("$[1].title", is("тест3"), String.class));
        this.mockMvc.perform(get("/users/3/events/revision"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        this.mockMvc.perform(get("/users/109/events/revision"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        createDto.setTitle("тест");
    }

    @Test
    @Transactional
    void test28_findAllWhereUserIsOwner() throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        createDto.setTitle("тест2");
        this.mockMvc.perform(post("/users/2/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(2L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        createDto.setTitle("тест3");
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        this.mockMvc.perform(get("/users/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].title", is("тест"), String.class))
                .andExpect(jsonPath("$[1].id", is(3L), Long.class))
                .andExpect(jsonPath("$[1].title", is("тест3"), String.class));
        this.mockMvc.perform(get("/users/3/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        this.mockMvc.perform(get("/users/109/events"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        createDto.setTitle("тест");
    }

    @Test
    @Transactional
    void test29_cancelEvent() throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        this.mockMvc.perform(patch("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("CANCELED"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        Event event = eventRepository.findById(1L).get();
        event.setState(EventState.REVISION);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("CANCELED"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        event.setState(EventState.PUBLISHED);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/users/1/events/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
    }

    @ParameterizedTest
    @ArgumentsSource(UpdateArgumentsProvider.class)
    @Transactional
    void test30_update(UpdateEventRequest updateDto) throws Exception {
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
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        UpdateEventRequest updateDto1 = UpdateEventRequest.builder()
                .eventId(1L)
                .title("update_title")
                .eventDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        updateDto1.setAnnotation("update_annotation");
        updateDto1.setEventDate(LocalDateTime.now().plusHours(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "HH:mm:ss")));
        this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.annotation", is(updateDto1.getAnnotation()), String.class));
        updateDto1.setCategory(8L);
        this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        updateDto1.setCategory(null);
        this.mockMvc.perform(patch("/users/404/events").content(mapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        Event event = eventRepository.findById(1L).get();
        event.setState(EventState.PUBLISHED);
        eventRepository.save(event);
        this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        event.setState(EventState.REVISION);
        eventRepository.save(event);
        if (updateDto.getAnnotation() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.annotation", is(updateDto.getAnnotation()), String.class));
        }
        if (updateDto.getDescription() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.description", is(updateDto.getDescription()), String.class));
        }
        if (updateDto.getTitle() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.title", is(updateDto.getTitle()), String.class));
        }
        if (updateDto.getEventDate() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.eventDate", is(updateDto.getEventDate()), String.class));
        }
        if (updateDto.getPaid() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.paid", is(updateDto.getPaid()), Boolean.class));
        }
        if (updateDto.getParticipantLimit() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.participantLimit", is(updateDto.getParticipantLimit()), Integer.class));
        }
        if (updateDto.getCategory() != null) {
            this.mockMvc.perform(patch("/users/1/events").content(mapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.category.id", is(updateDto.getCategory()), Long.class));
        }
    }

    @Test
    @Transactional
    void test31_findAllRequestsByEventId() throws Exception {
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
        createDto.setParticipantLimit(0);
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        Event event = eventRepository.findById(1L).get();
        event.setState(EventState.PUBLISHED);
        event.setRequestModeration(false);
        eventRepository.save(event);
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(post("/users/3/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].event", is(1L), Long.class))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$[0].requester", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].event", is(1L), Long.class))
                .andExpect(jsonPath("$[1].status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$[1].requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(get("/users/3/events/1/requests"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(get("/users/1/events/404/requests"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    @Test
    @Transactional
    void test32_confirmRequest() throws Exception {
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
        createDto.setParticipantLimit(1);
        createDto.setRequestModeration(true);
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        Event event = eventRepository.findById(1L).get();
        event.setState(EventState.PUBLISHED);
        eventRepository.save(event);
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("PENDING"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(post("/users/3/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("PENDING"), String.class))
                .andExpect(jsonPath("$.requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(patch("/users/1/events/1/requests/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].event", is(1L), Long.class))
                .andExpect(jsonPath("$[0].status", is("CONFIRMED"), String.class))
                .andExpect(jsonPath("$[0].requester", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].event", is(1L), Long.class))
                .andExpect(jsonPath("$[1].status", is("CANCELED"), String.class))
                .andExpect(jsonPath("$[1].requester", is(user2.getId()), Long.class));
        this.mockMvc.perform(patch("/users/2/events/1/requests/1/confirm"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(patch("/users/1/events/1/requests/404/confirm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
    }

    @Test
    @Transactional
    void test33_rejectRequest() throws Exception {
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
        createDto.setParticipantLimit(1);
        createDto.setRequestModeration(true);
        this.mockMvc.perform(post("/users/1/events").content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(1L), Long.class))
                .andExpect(jsonPath("$.state", is("PENDING"), String.class))
                .andExpect(jsonPath("$.title", is(createDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(createDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(createDto.getDescription()), String.class));
        Event event = eventRepository.findById(1L).get();
        event.setState(EventState.PUBLISHED);
        eventRepository.save(event);
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("PENDING"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
        this.mockMvc.perform(patch("/users/2/events/1/requests/1/reject"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.reason", is("Не выполнены условия для совершения операции"), String.class));
        this.mockMvc.perform(patch("/users/1/events/1/requests/404/reject"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Запрашиваемый объект не найден"), String.class));
        this.mockMvc.perform(patch("/users/1/events/1/requests/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("REJECTED"), String.class))
                .andExpect(jsonPath("$.requester", is(user1.getId()), Long.class));
    }

    /**
     * создание окружения
     */
    @BeforeEach
    private void createEnvironment() {
        category = categoryRepository.save(category);
        categoryRepository.save(category1);
        user = userRepository.save(user);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
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

    static class UpdateArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(UpdateEventRequest.builder().eventId(1).annotation("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).description("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).title("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).
                            eventDate(LocalDateTime.now().plusDays(5)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).paid(true).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).participantLimit(50).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(1).category(2L).build())
            );
        }
    }
}