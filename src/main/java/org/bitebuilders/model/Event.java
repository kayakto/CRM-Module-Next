package org.bitebuilders.model;

import lombok.*;
import org.bitebuilders.controller.dto.EventDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Table("events")
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class Event {
    @Id
    private Long id;

    @Column("title")
    private String title;

    @Column("status")
    private Status status;

    @Column("description")
    private String description;

    @Column("admin_id")
    private Long adminId;

    @Column("enrollment_start_date")
    private OffsetDateTime enrollmentStartDate;

    @Column("enrollment_end_date")
    private OffsetDateTime enrollmentEndDate;

    @Column("number_seats_students")
    private int numberSeatsStudent;

    @Column("event_start_date")
    private OffsetDateTime eventStartDate;

    @Column("event_end_date")
    private OffsetDateTime eventEndDate;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Column("has_test")
    private boolean hasTest; // default false in db

    @Column("chat_url")
    private String chatUrl;

    public enum Status {
        PREPARATION, // Статус "Подготовка" : (текущая дата < enrollment_start_date)
        REGISTRATION_OPEN, // Статус "Регистрация открыта" : (enrollment_start_date <= текущая дата <= enrollment_end_date, number_seats > 0).
        NO_SEATS, // Статус "Мест нет" : (enrollment_start_date <= текущая дата <= enrollment_end_date, number_seats=0).
        REGISTRATION_CLOSED, // Статус "Регистрация закрыта" : (enrollment_end_date < текущая дата < event_start_date)
        IN_PROGRESS, // Статус "В процессе проведения" : (event_start_date <= текущая дата <= event_end_date)
        FINISHED, // Статус "Завершено" : (текущая дата > event_end_date)
        HIDDEN, // Статус "Скрыто" : Мероприятие вручную скрыто администратором
        DELETED // Статус "Удалено" : Мероприятие вручную удалено администратором
    }

    public void setConditionToStarted() {
        this.enrollmentEndDate = OffsetDateTime.now();
        this.eventStartDate = OffsetDateTime.now();
        this.status = Status.IN_PROGRESS;
    }

    public Event(String description, String title, Long adminId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, String chatUrl, int numberSeatsStudent, boolean hasTest) {
        this.description = description;
        this.title = title;
        this.adminId = adminId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.chatUrl = chatUrl;
        this.numberSeatsStudent = numberSeatsStudent;
        this.hasTest = hasTest;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public Event(Long id, String description, String title, Long adminId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, String chatUrl, int numberSeatsStudent, boolean hasTest) {
        this.id = id;
        this.description= description;
        this.title = title;
        this.adminId = adminId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.chatUrl = chatUrl;
        this.numberSeatsStudent = numberSeatsStudent;
        this.hasTest = hasTest;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public EventDTO toEventDTO() {
        return new EventDTO(id, status, description,
                title, adminId,
                eventStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                eventEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                chatUrl,
                enrollmentStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                enrollmentEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                numberSeatsStudent, hasTest, createdAt, updatedAt);
    }
}
