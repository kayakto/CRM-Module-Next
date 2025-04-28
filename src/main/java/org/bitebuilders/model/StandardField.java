package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@Table("standard_fields")
public class StandardField {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("isRequired")
    private boolean isRequired;

    @Column("displayOrder")
    private int displayOrder;

    @Column("options")
    private List<String> options;
}
