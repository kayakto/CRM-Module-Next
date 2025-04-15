package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("status_triggers")
public class StatusTrigger {
    @Column("status_id")
    private Long statusId;

    @Column("trigger_id")
    private Long triggerId;

    @Column("executed")
    private Boolean executed;
}
