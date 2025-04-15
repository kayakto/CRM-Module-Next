package org.bitebuilders.converter.time;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@ReadingConverter
public class OffsetDateTimeReadDateConverter implements Converter<Date, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(Date source) {
        if (source == null) {
            throw new IllegalArgumentException("Date can't be null");
        }
        // Преобразуем java.sql.Date в LocalDate, затем в OffsetDateTime
        LocalDate localDate = source.toLocalDate();
        return localDate.atStartOfDay().atOffset(ZoneOffset.ofHours(5));
    }
}
