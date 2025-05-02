package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("system_fields")
public class SystemField {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("isRequired")
    private Boolean isRequired;

    @Column("displayOrder")
    private Integer displayOrder;
}
