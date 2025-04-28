package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("application_statuses")
@NoArgsConstructor
public class ApplicationStatus {
    @Id
    private Long id;

    @Column("event_id")
    private Long eventId;

    @Column("name")
    private String name;

    @Column("is_system")
    private Boolean isSystem = false;

    @Column("display_order")
    private Integer displayOrder;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    public ApplicationStatus(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
        this.updatedAt = OffsetDateTime.now();
    }

    // Метод для проверки системного статуса
    public boolean isSystemStatus() {
        return Boolean.TRUE.equals(isSystem);
    }
}
