package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "First name is required")
    private final String firstName;
    @NotBlank(message = "Last name is required")
    private final String lastName;
    private final String surname;
    @NotBlank(message = "Telegram is required")
    private final String telegramUrl;
    @NotBlank(message = "Vk is required")
    private final String vkUrl;
}
