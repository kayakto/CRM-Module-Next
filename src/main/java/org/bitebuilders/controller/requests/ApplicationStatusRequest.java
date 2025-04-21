package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.bitebuilders.model.ApplicationStatus;

@Data
@AllArgsConstructor
@Getter
@ToString
public class ApplicationStatusRequest {
    private String name;
    private Integer displayOrder;

    public ApplicationStatus toApplicationStatus() {
        return new ApplicationStatus(name, displayOrder);
    }
}
