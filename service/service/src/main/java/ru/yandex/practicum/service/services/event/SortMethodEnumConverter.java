package ru.yandex.practicum.service.services.event;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.model.SortMethod;

import java.util.Locale;

/**
 * конвертер методов сортировки без учёта регистра написания в запросе
 */
@Component
public class SortMethodEnumConverter implements Converter<String, SortMethod> {
    @Override
    public SortMethod convert(String source) {
        try {
            return source.isEmpty() ? null : SortMethod.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return SortMethod.UNSUPPORTED_METHOD;
        }
    }
}