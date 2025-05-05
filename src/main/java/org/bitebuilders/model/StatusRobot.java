package org.bitebuilders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("robots")
@NoArgsConstructor
public class StatusRobot {
    @Column("statusId")
    private Long statusId;

    @Column("robotId")
    private Long robotId;

    @Column("position")
    private Integer position;

    @Column("executedAt")
    private OffsetDateTime executedAt;

    public StatusRobot(Long statusId, Long robotId, Integer position) {
        this.statusId = statusId;
        this.robotId = robotId;
        this.position = position;
    }
}

