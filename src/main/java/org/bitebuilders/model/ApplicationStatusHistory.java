package org.bitebuilders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("application_status_history")
@NoArgsConstructor
public class ApplicationStatusHistory {
    @Id
    private Long id;

    @Column("application_id")
    private Long applicationId;

    @Column("from_status_id")
    private Long fromStatusId;

    @Column("to_status_id")
    private Long toStatusId;

    @Column("changed_at")
    private OffsetDateTime changedAt;
}
