package org.bitebuilders.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("events_tests")
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EventTest {
    @Id
    private Long id;
    @Column("event_id")
    private Long eventId;
    @Column("test_url")
    private String testUrl;

    public EventTest(Long eventId, String testUrl) {
        this.eventId = eventId;
        this.testUrl = testUrl;
    }
}
