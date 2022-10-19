package ru.yandex.practicum.service.services.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exceptions.EmailUsedException;
import ru.yandex.practicum.service.exceptions.NotFoundException;
import ru.yandex.practicum.service.model.OffsetLimitPageable;
import ru.yandex.practicum.service.model.User;
import ru.yandex.practicum.service.model.dto.NewUserRequest;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.model.mappers.UserMapper;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис пользователей для администраторов
 */
@Service
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;

    @Autowired
    public UserAdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest dto) {
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
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.unsorted());
        if (ids==null) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            ids.forEach(this::validateUserId);
            return userRepository.findByIds(ids, pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    /**
     * метод проверки существования пользователя по id
     *
     * @param id - id пользователя из запроса
     */

    public void validateUserId(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id '%s' не найден.", id));
        }
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
}
