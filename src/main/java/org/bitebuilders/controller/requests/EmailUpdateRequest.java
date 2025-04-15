package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@ToString
public class EmailUpdateRequest {
    @Email(message = "Invalid email format")
    private String oldEmail;
    @Email(message = "Invalid email format")
    private String newEmail;
}
