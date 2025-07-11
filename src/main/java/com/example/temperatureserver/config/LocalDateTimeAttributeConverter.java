package com.example.temperatureserver.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime locDateTime) {
        if (locDateTime == null) {
            return null;
        }
        return locDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dbData), ZoneId.systemDefault());
    }
}
