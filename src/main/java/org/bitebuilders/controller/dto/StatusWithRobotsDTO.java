package org.bitebuilders.controller.dto;

import java.util.List;

public record StatusWithRobotsDTO(
        Long statusId,
        List<RobotDTO> robots
) {}
