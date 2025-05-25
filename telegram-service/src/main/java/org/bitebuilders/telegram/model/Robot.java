package org.bitebuilders.telegram.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Table("robots")
public class Robot {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("parameters")
    private Map<String, Object> parameters;

    @Column("created_at")
    private OffsetDateTime createdAt;
}
