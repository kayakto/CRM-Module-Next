package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Setter
@Table("application_statuses")
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
}
