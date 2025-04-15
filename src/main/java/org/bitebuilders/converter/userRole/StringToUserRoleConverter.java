package org.bitebuilders.converter.userRole;

import org.bitebuilders.enums.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class StringToUserRoleConverter implements Converter<String, UserRole> {
    @Override
    public UserRole convert(String source) {
        return UserRole.valueOf(source);
    }
}
