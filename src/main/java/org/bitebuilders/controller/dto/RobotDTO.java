package org.bitebuilders.controller.dto;

import java.util.Map;

public record RobotDTO(
        Long id,
        Long statusId,
        String name,
        String type,
        Map<String, Object> parameters,
        int position
) {}
