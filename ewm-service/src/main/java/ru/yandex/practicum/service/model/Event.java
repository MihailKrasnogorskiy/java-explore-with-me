package ru.yandex.practicum.service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс событий
 */
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created")
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private boolean paid;
    private String title;
    @Transient
    private long views;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "admin_comment")
    private String adminComment;
    @Column(name = "lat", nullable = false)
    private Float lat;
    @Column(name = "lon", nullable = false)
    private Float lon;
}
