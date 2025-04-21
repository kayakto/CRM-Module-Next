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
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatus {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("is_system")
    private Boolean isSystem;

    @Column("display_order")
    private Integer displayOrder;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    public ApplicationStatus(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
        isSystem = false;
        updatedAt = OffsetDateTime.now();
    }
}
