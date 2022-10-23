package ru.yandex.practicum.service.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.ParticipationRequest;
import ru.yandex.practicum.service.model.RequestStatus;

import java.util.List;
import java.util.Optional;

/**
 * репозиторий запросов на участие в событиях
 */
@Repository
public interface RequestRepository extends CrudRepository<ParticipationRequest, Long> {
    /**
     * получение списка всех запросов пользователя на участие в чужих событиях
     *
     * @param userId - id пользователя
     * @return - список заявок
     */
    List<ParticipationRequest> findAllByRequesterId(long userId);

    /**
     * получение списка запросов на участие в событии
     *
     * @param eventId - id события
     * @return - список заявок
     */
    List<ParticipationRequest> findAllByEventId(long eventId);

    /**
     * проверка существования запроса на участие в событии по событию и пользователю
     *
     * @param eventId - id события
     * @param userId  - id пользователя
     * @return - boolean
     */
    Boolean existsByEventIdAndRequesterId(long eventId, long userId);

    /**
     * получение запроса на участие в событии по событию и пользователю
     *
     * @param eventId - id события
     * @param userId  - id пользователя
     * @return - запрос на участие в событии
     */
    Optional<ParticipationRequest> findByEventIdAndRequesterId(long eventId, long userId);

    /**
     * проверка владельца запроса
     *
     * @param id     - id запроса
     * @param userId - id пользователя
     * @return - boolean
     */
    boolean existsByIdAndRequesterId(long id, long userId);

    /**
     * количество заявок на участие в событии
     *
     * @param eventId - id события
     * @param status  - статус заявки
     * @return - количество заявок
     */
    int countByEventIdAndStatus(long eventId, RequestStatus status);

    /**
     * получение списка запросов на участие в событии
     *
     * @param eventId - id события
     * @param status  - игнорируемый статус заявки
     * @return - список заявок
     */
    //  @Query(value = "SELECT * FROM requests AS r WHERE r.id = ?1 AND r.status = 2", nativeQuery = true)
    List<ParticipationRequest> findByEventIdAndStatusNot(long eventId, RequestStatus status);
}
