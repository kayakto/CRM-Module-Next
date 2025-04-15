package org.bitebuilders.controller.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RefreshTokenRequest {
    private String refresh;
}
