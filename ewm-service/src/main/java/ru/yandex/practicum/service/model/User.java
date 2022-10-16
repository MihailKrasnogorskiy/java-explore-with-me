package ru.yandex.practicum.service.model;

import lombok.*;

import javax.persistence.*;

/**
 * класс пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String name;
}
