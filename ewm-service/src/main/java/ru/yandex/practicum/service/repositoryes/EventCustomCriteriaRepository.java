package ru.yandex.practicum.service.repositoryes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.model.Event;
import ru.yandex.practicum.service.model.EventState;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventCustomCriteriaRepository {

    private final EntityManager entityManager;

    @Autowired
    public EventCustomCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    /**
     * запрос событий с возможностью фильтрации
     *
     * @param text       - текст для поиска в содержимом аннотации и подробном описании события
     * @param categories - список идентификаторов категорий в которых будет вестись поиск
     * @param paid       - поиск только платных/бесплатных событий
     * @param rangeStart - дата и время не раньше которых должно произойти событие
     * @param rangeEnd   - дата и время не позже которых должно произойти событие
     * @param from       - количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       - количество событий в наборе
     * @param users      - список id пользователей, чьи события нужно найти
     * @param states     - список состояний в которых находятся искомые события
     * @return - список событий
     */

    public List<Event> findAll(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd, Integer from,
                               Integer size, List<Long> users, List<EventState> states) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        if (text != null) {
            predicates.add(cb.or(cb.like(cb.upper(event.get("annotation")), "%" + text.toUpperCase() + "%"),
                    cb.like(cb.upper(event.get("description")), "%" + text.toUpperCase() + "%")));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates.add(cb.in(event.get("category").get("id")).value(categories));
        }
        if (paid != null) {
            predicates.add(cb.equal(event.get("paid"), paid));
        }
        if (rangeStart != null) {
            predicates.add(cb.greaterThan(event.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            predicates.add(cb.lessThan(event.get("eventDate"), rangeEnd));
        }
        if (users != null && !users.isEmpty()) {
            predicates.add(cb.in(event.get("initiator").get("id")).value(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(cb.in(event.get("state")).value(states));
        }

        return entityManager.createQuery(query.select(event).where(cb.and(predicates.toArray(Predicate[]::new))))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}