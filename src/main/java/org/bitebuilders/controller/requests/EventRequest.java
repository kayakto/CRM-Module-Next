package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bitebuilders.model.Event;

import java.time.OffsetDateTime;

@Data
@Getter
@AllArgsConstructor
public class EventRequest {
    private final String title;
    private final String description;
    private final Long adminId;
    private final OffsetDateTime eventStartDate;
    private final OffsetDateTime eventEndDate;
    private final OffsetDateTime enrollmentStartDate;
    private final OffsetDateTime enrollmentEndDate;
    private final int numberSeatsStudent;

    public Event toEvent() {
        return new Event(description, title,
                adminId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, numberSeatsStudent);
    }

    public Event toEvent(Long eventId) {
        return new Event(eventId,
                description, title,
                adminId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, numberSeatsStudent);
    }
}
