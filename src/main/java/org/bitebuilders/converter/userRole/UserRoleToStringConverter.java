package org.bitebuilders.converter.userRole;

import org.bitebuilders.enums.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class UserRoleToStringConverter implements Converter<UserRole, String> {
    @Override
    public String convert(UserRole userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException("userRole cannot be null");
        }
        return userRole.name();
    }
}
