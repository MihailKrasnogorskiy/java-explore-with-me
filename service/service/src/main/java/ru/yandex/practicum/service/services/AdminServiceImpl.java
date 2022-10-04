package ru.yandex.practicum.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.controllers.OffsetLimitPageable;
import ru.yandex.practicum.service.exceptions.EmailUsedException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.model.mappers.UserMapper;
import ru.yandex.practicum.service.repositoryes.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * класс сервиса администраторов
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    private UserRepository repository;

    @Autowired
    public AdminServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        validateUserEmail(dto.getEmail());
        User user = repository.save(UserMapper.toUser(dto));
        log.info("Создан объект пользователя {} ", user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        validateUserId(id);
        repository.deleteById(id);
        log.info("Пользователь с id {} был удалён", id);
    }

    /**
     * метод проверки существования пользователя по id
     *
     * @param id - id пользователя из запроса
     * @return true или выбрасывание исключения
     */
    @Override
    public boolean validateUserId(long id) {
        if (!repository.getAllUsersId().contains(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return true;
        }
    }

    @Override
    @Transactional
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        if (ids.isEmpty()) {
            return repository.findAll(pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return repository.findByIds(ids, pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    /**
     * метод проверки регистрации другого пользователя на уже зарегистрированный email
     *
     * @param email - email адрес из запроса
     */
    private void validateUserEmail(String email) {
        if (repository.getAllUsersEmail().contains(email)) {
            throw new EmailUsedException(email);
        }
    }
}
