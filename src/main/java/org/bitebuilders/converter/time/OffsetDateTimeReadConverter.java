package org.bitebuilders.converter.time;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@ReadingConverter
public class OffsetDateTimeReadConverter implements Converter<Timestamp, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(Timestamp source) {
        if (source == null) {
            throw new IllegalArgumentException("Date can`t be null");
        }
        return source.toInstant().atOffset(ZoneOffset.ofHours(5));
    } // работает
}

