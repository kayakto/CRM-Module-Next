package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Invalid email format")
    public String email;
    public String password;
}
