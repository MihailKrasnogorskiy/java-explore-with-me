package ru.yandex.practicum.service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.exceptions.EmailUsedException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.model.mappers.UserMapper;
import ru.yandex.practicum.service.repositoryes.UserRepository;

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
    public UserDto createUser(UserCreateDto dto) {
        validateUserEmail(dto.getEmail());
        User user = repository.save(UserMapper.toUser(dto));
        log.info("Создан объект пользователя {} ", user);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(long id) {
        validateUserId(id);
        repository.deleteById(id);
        log.info("Пользователь с id {} был удалён", id);
    }

    /**
     * метод проверки существования пользователя по id
     *
     * @param id - id пользователя из запроса
     */
    @Override
    public void validateUserId(long id) {
        if (!repository.getAllUsersId().contains(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
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
