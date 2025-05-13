package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
public class LinkTriggerToStatusRequest {

    @NotNull
    private Long triggerId;

    @NotNull
    private Map<String, Object> parameters;
}
