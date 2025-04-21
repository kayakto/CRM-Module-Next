package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@Table("form_fields")
@AllArgsConstructor
@NoArgsConstructor
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

    @Transient // чтобы Spring JDBC проигнорировал
    private List<String> options;

    @Column("options") // Это колонка, куда кладётся JSON
    private String optionsJson;

    public FormField(String name, String type, Boolean isRequired,
                     Integer displayOrder, List<String> options) {
        this.name = name;
        this.type = type;
        this.isRequired = isRequired;
        this.displayOrder = displayOrder;
        this.options = options;
    }
}


