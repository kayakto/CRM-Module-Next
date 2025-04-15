package org.bitebuilders.converter.status;

import org.bitebuilders.model.Event;
import org.springframework.core.convert.converter.Converter;

import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class StatusToStringConverter implements Converter<Event.Status, String> {
    @Override
    public String convert(Event.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can`t be null");
        }
        return status.name();
    }
}


