package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitebuilders.model.Robot;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusRobotWithRobot {
    private Long statusId;
    private Robot robot;
    private int position;
    private OffsetDateTime executedAt;
}

