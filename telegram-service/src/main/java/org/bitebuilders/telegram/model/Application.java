package org.bitebuilders.telegram.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("applications")
public class Application {
    @Id
    private Long id;

    @Column("event_id")
    private Long eventId;

    @Column("status_id")
    private Long statusId;

    @Column("form_data")
    private String formData;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;
}