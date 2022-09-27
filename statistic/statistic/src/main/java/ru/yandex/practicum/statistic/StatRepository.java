package ru.yandex.practicum.statistic;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.statistic.model.EndpointHit;

@Repository
public interface StatRepository extends CrudRepository<EndpointHit, Long> {
}
