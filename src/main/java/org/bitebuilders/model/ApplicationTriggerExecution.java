package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("application_trigger_executions")
public class ApplicationTriggerExecution {
    @Column("application_id")
    private Long applicationId;

    @Column("status_id")
    private Long statusId;

    @Column("trigger_id")
    private Long triggerId;

    @Column("executed")
    private Boolean executed;

    @Column("executed_at")
    private OffsetDateTime executedAt;
}
