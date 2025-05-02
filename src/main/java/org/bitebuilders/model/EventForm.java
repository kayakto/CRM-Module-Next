package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.controller.dto.EventDTO;
import org.bitebuilders.controller.dto.FormDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.List;

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
    private Boolean isTemplate = false;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Transient
    private List<FormField> fields;

    @Transient
    private List<SystemField> systemFields;
}
