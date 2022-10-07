package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.ParticipationRequest;
import ru.yandex.practicum.service.model.RequestStatus;

import java.util.List;

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
     * проверка владельца запроса
     *
     * @param Id     - id запроса
     * @param userId - id пользователя
     * @return - boolean
     */
    boolean existsByIdAndRequesterId(long Id, long userId);

    /**
     * количество заявок на участие в событии
     *
     * @param eventId - id события
     * @param status  - статус заявки
     * @return - количество заявок
     */
    @Query(value = "SELECT COUNT(r.id) FROM requests AS r WHERE r.id = ?1 AND r.status LIKE ?2", nativeQuery = true)
    int countAllByEventIdAndStatus(long eventId, String status);

    /**
     * получение списка запросов на участие в событии
     *
     * @param eventId - id события
     * @param status  - игнорируемый статус заявки
     * @return - список заявок
     */
    @Query(value = "SELECT * FROM requests AS r WHERE r.id = ?1 AND r.status NOT LIKE ?2", nativeQuery = true)
    List<ParticipationRequest> findAllByEventIdAndNotStatus(long eventId, String status);
}
