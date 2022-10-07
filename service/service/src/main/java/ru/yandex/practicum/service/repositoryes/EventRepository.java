package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByCategoryId(long categoryId);

    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    /**
     * проверка владельца события
     *
     * @param id     - id события
     * @param userId - id пользователя
     * @return - boolean
     */
    boolean existsByIdAndInitiatorId(long id, long userId);

    @Query(value = "SELECT e.participant_limit FROM events AS e WHERE e.id = ?1", nativeQuery = true)
    int findParticipantLimitById(long id);
}
