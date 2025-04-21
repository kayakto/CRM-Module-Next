package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("event_forms")
public class EventForm {
    @Id
    private Long id;

    @Column("event_id")
    private Long eventId;

    @Column("title")
    private String title;

    @Column("is_template")
    private Boolean isTemplate;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;
}
