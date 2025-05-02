package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bitebuilders.model.Event;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EventDTO {
    private final Long id;
    private final Event.Status status;
    private final String description;
    private final String title;
    private final Long adminId;
    private final OffsetDateTime eventStartDate;
    private final OffsetDateTime eventEndDate;
    private final OffsetDateTime enrollmentStartDate;
    private final OffsetDateTime enrollmentEndDate;
    private final int numberSeatsStudent;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
