package org.bitebuilders.converter.statusRequest;

import org.bitebuilders.enums.StatusRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class StringToStatusRequestConverter implements Converter<String, StatusRequest> {
    @Override
    public StatusRequest convert(String source) {
        return StatusRequest.valueOf(source);
    }
}
