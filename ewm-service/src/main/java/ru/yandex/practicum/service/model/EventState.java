package ru.yandex.practicum.service.model;

/**
 * перечисление внутренних состояний события
 */
public enum EventState {
    PENDING, PUBLISHED, CLOSED, CANCELED, REVISION, RESEND
}
