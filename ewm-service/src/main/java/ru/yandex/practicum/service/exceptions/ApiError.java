package ru.yandex.practicum.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * класс ошибки
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private List<String> errors = new ArrayList<>();
    private String message;
    private String reason;
    private String status;
    private String timestamp;


}