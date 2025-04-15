package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("form_fields")
public class FormField {
    @Id
    private Long id;

    @Column("form_id")
    private Long formId;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("is_required")
    private Boolean isRequired;

    @Column("display_order")
    private Integer displayOrder;
}
