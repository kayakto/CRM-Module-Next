package org.bitebuilders.telegram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApplicationStatusChangedEvent {
    private final Long applicationId;
    private final Long newStatusId;
}
