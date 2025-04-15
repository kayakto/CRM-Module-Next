package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Table("triggers")
public class Trigger {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("created_at")
    private String parameters; // Или JsonNode

    @Column("created_at")
    private OffsetDateTime createdAt;
}
