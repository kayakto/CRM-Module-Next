package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.model.UserInfo;

@Data
@Getter
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private String surname;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String sign; // Password
    @NotBlank(message = "Telegram is required")
    private String telegramUrl;
    @NotBlank(message = "Vk is required")
    private String vkUrl;

    public UserInfo toUserInfo(UserRole role, String sign) {
        return new UserInfo(
                this.firstName,
                this.lastName,
                this.surname,
                this.email,
                sign,
                this.vkUrl,
                this.telegramUrl,
                role
        );
    }
}
