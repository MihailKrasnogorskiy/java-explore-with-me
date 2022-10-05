package ru.yandex.practicum.service.services;

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
import ru.yandex.practicum.service.model.dto.CategoryDto;
import ru.yandex.practicum.service.model.dto.NewCategoryDto;
import ru.yandex.practicum.service.model.dto.UserCreateDto;
import ru.yandex.practicum.service.model.dto.UserDto;
import ru.yandex.practicum.service.model.mappers.CategoryMapper;
import ru.yandex.practicum.service.model.mappers.UserMapper;
import ru.yandex.practicum.service.repositoryes.CategoryRepository;
import ru.yandex.practicum.service.repositoryes.UserRepository;

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

    @Autowired
    public AdminServiceImpl(UserRepository repository, CategoryRepository categoryRepository) {
        this.userRepository = repository;
        this.categoryRepository = categoryRepository;
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
        // TODO описать проверку отсутствия эвентов
        categoryRepository.deleteById(id);
        log.info("Категория с id {} была удалена", id);
    }

    /**
     * метод проверки регистрации другого пользователя на уже зарегистрированный email
     *
     * @param email - email адрес из запроса
     */
    @Transactional
    void validateUserEmail(String email) {
        if (userRepository.getAllUsersEmail().contains(email)) {
            throw new EmailUsedException(email);
        }
    }

    /**
     * метод проверки существования пользователя по id
     *
     * @param id - id пользователя из запроса
     * @return true или выбрасывание исключения
     */
    @Override
    @Transactional
    public boolean validateUserId(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        } else {
            return true;
        }
    } // TODO разобраться надо ли возвращать true
}
