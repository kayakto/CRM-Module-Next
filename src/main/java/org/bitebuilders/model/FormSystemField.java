package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("form_system_fields")
public class FormSystemField {
    @Column("form_id")
    private Long formId;

    @Column("system_field_id")
    private Long systemFieldId;

    @Column("is_required")
    private Boolean isRequired = true;

    @Column("display_order")
    private Integer displayOrder;
}
