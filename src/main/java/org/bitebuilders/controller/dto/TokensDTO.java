package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class TokensDTO {
    private String access;
    private String refresh;
}
