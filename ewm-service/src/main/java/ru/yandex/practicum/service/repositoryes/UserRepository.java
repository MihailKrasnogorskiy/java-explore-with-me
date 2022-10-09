package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.User;

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

    @Query("select u.id from User as u")
    List<Long> getAllUsersId();

    /**
     * получение списка email пользователей
     *
     * @return - список email пользователей
     */

    @Query("select u.email from User as u")
    List<String> getAllUsersEmail();


    Page<User> findAll(Pageable pageable);

    /**
     * запрос списка пользователей по id
     *
     * @param ids      - список id пользователей
     * @param pageable - объект для создания запросов на перелистывание страниц
     * @return - список пользователей
     */
    @Query(value = "SELECT u FROM User AS u WHERE u.id in :ids")
    List<User> findByIds(List<Long> ids, Pageable pageable);
}
