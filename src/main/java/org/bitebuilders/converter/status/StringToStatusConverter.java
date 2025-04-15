package org.bitebuilders.converter.status;

import org.bitebuilders.model.Event;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;


@Component
@ReadingConverter
public class StringToStatusConverter implements Converter<String, Event.Status> {
    @Override
    public Event.Status convert(String source) {
        return Event.Status.valueOf(source);
    }
}
