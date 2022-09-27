package ru.yandex.practicum.service.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean pinned;
    private String title;
    @OneToMany(mappedBy = "compilations", fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    public Compilation() {

    }
}
