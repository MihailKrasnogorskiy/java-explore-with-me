package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.User;

import javax.transaction.Transactional;
import java.util.List;

/**
 * репозиторий пользователей
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * получение списка id пользователей
     *
     * @return список id пользователей
     */
    @Transactional
    @Query("select u.id from User as u")
    List<Long> getAllUsersId();

    /**
     * получение списка email пользователей
     *
     * @return - список email пользователей
     */
    @Transactional
    @Query("select u.email from User as u")
    List<String> getAllUsersEmail();
}
