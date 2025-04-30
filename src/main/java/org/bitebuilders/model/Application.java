package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Table("applications")
public class Application {
    @Id
    private Long id;

    @Column("event_id")
    private Long eventId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("surname")
    private String surname;

    @Column("email")
    private String email;

    @Column("telegram_url")
    private String telegramUrl;

    @Column("status_id")
    private Long statusId;

    @Column("form_data")
    private String formData;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;
}
