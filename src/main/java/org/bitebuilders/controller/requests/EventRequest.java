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
    private final String descriptionText;
    private final Long adminId;
    private final OffsetDateTime eventStartDate;
    private final OffsetDateTime eventEndDate;
    private final OffsetDateTime enrollmentStartDate;
    private final OffsetDateTime enrollmentEndDate;
    private final String chatUrl;
    private final int numberSeatsStudent;
    private final boolean hasTest;
    private final String testUrl;

    public Event toEvent() {
        return new Event(descriptionText, title,
                adminId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, chatUrl, numberSeatsStudent, hasTest);
    }

    public Event toEvent(Long eventId) {
        return new Event(eventId,
                descriptionText, title,
                adminId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, chatUrl, numberSeatsStudent, hasTest);
    }
}
