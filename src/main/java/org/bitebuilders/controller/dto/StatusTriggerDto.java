package org.bitebuilders.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class StatusTriggerDto {
    private Long id;                          // triggerId
    private String name;                      // trigger.name
    private String type;                      // trigger.type
    private Map<String, Object> parameters;   // из status_trigger

    public StatusTriggerDto(Long id, String name, String type, Map<String, Object> parameters) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }
}
