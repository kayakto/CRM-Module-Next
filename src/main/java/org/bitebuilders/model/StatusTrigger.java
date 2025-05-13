package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("status_triggers")
public class StatusTrigger {
    @Column("status_id")
    private Long statusId;

    @Column("trigger_id")
    private Long triggerId;

    @Column("executed")
    private Boolean executed;

    @Column("parameters")
    private Map<String, Object> parameters;
}
