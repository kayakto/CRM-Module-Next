package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitebuilders.model.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer displayOrder;

    public ApplicationStatus toApplicationStatus() {
        return new ApplicationStatus(name, displayOrder);
    }
}
