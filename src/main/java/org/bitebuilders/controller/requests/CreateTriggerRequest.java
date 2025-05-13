package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Getter
public class CreateTriggerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private Map<String, Object> parameters;
}
