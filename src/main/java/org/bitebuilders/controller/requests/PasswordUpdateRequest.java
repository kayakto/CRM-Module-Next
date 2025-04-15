package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor
@Getter
@ToString
public class PasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;
}
