package org.bitebuilders.controller.dto;

import lombok.*;
import org.bitebuilders.enums.UserRole;

import java.time.OffsetDateTime;

@Data
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String surname;
    private String email;
    private String vkUrl;
    private UserRole role_enum;
    private String telegramUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
