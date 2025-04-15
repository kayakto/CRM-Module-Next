package org.bitebuilders.converter.statusRequest;

import org.bitebuilders.enums.StatusRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class StatusRequestToStringConverter implements Converter<StatusRequest, String> {
    @Override
    public String convert(StatusRequest statusRequest) {
        if (statusRequest == null) {
            throw new IllegalArgumentException("StatusRequest cannot be null");
        }
        return statusRequest.name();
    }
}
