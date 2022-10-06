package ru.yandex.practicum.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.*;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;
import ru.yandex.practicum.service.model.OffsetLimitPageable;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.*;
import ru.yandex.practicum.service.model.mappers.CategoryMapper;
import ru.yandex.practicum.service.model.mappers.EventMapper;
import ru.yandex.practicum.service.model.mappers.UserMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;
import ru.yandex.practicum.service.repositoryes.EventRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * класс сервиса администраторов
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private EventRepository eventRepository;
    private EventMapper eventMapper;
    private final int PUBLISH_TIME_LAG = 2;

    @Autowired
    public AdminServiceImpl(UserRepository repository, CategoryRepository categoryRepository,
                            EventRepository eventRepository, EventMapper eventMapper) {
        this.userRepository = repository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        validateUserEmail(dto.getEmail());
        User user = userRepository.save(UserMapper.toUser(dto));
        log.info("Создан объект пользователя {} ", user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        validateUserId(id);
        userRepository.deleteById(id);
        log.info("Пользователь с id {} был удалён", id);
    }

    @Override
    @Transactional
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        if (ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findByIds(ids, pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository
                .save(CategoryMapper.toCategoryFromNewCategoryDto(dto)));
        log.info("Категория {} с id = {} была создана", categoryDto.getName(), categoryDto.getId());
        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto dto) {
        if (categoryRepository.existsById(dto.getId())) {
            CategoryDto categoryDto = CategoryMapper.toDto(categoryRepository
                    .save(CategoryMapper.toCategoryFromCategoryDto(dto)));
            log.info("Категория с id = {} была обновлена, новое имя - {}", categoryDto.getId(), categoryDto.getName());
            return categoryDto;
        } else {
            throw new NotFoundException("Категория с id = " + dto.getId() + " не найдена");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        if (eventRepository.findAllByCategoryId(id).isEmpty()) {
            categoryRepository.deleteById(id);
            log.info("Категория с id {} была удалена", id);
        } else {
            throw new CategoryUsedException(id);
        }
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (!event.getCreatedOn().plusHours(PUBLISH_TIME_LAG).isBefore(event.getEventDate())) {
            throw new EventDateValidationException(PUBLISH_TIME_LAG);
        }
        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);
        log.info("Событие с id = {} опубликовано", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateValidationException();
        }
        event.setState(EventState.CANCELED);
        log.info("Событие с id = {} отклонено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    /**
     * метод проверки регистрации другого пользователя на уже зарегистрированный email
     *
     * @param email - email адрес из запроса
     */

    private void validateUserEmail(String email) {
        if (userRepository.getAllUsersEmail().contains(email)) {
            throw new EmailUsedException(email);
        }
    }

    /**
     * метод проверки существования пользователя по id
     *
     * @param id - id пользователя из запроса
     */
    @Override
    public void validateUserId(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    /**
     * проверка на существование события по id
     *
     * @param eventId - id события
     */
    private void validateEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id = " + eventId + " не найдено");
        }
    }
}
