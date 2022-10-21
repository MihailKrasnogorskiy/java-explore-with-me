package ru.yandex.practicum.service.exceptions;

public class HttpClientException extends RuntimeException{
    public HttpClientException(String message) {
        super(message);
    }
}
