package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Table("robots")
public class Robot {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("parameters")
    private String parameters;

    @Column("created_at")
    private OffsetDateTime createdAt;
}
