package ru.yandex.practicum.statistic;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.statistic.model.EndpointHit;

import java.time.LocalDateTime;

@Repository
public interface StatRepository extends CrudRepository<EndpointHit, Long> {
    @Query(value = "SELECT COUNT(uri) " +
            "from hits " +
            "where uri = ?3 " +
            "and views_date >= ?1 and views_date<= ?2 " +
            "GROUP BY ip",
            nativeQuery = true)
    Long getViewUnique(LocalDateTime startTime, LocalDateTime endTime, String uri);

    @Query(value = "SELECT COUNT(uri) " +
            "from hits " +
            "where uri = ?3 " +
            "and views_date >= ?1 and views_date<= ?2 "
            , nativeQuery = true)
    Long getView(LocalDateTime startTime, LocalDateTime endTime, String uri);
}
