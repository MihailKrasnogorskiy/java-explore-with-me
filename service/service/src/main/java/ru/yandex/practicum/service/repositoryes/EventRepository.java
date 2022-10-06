package ru.yandex.practicum.service.repositoryes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByCategoryId(long categoryId);
}
