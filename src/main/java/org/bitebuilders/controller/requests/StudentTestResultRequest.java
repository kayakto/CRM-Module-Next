package org.bitebuilders.controller.requests;

import lombok.*;
import org.bitebuilders.model.StudentTestResult;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentTestResultRequest {
    private Long studentId;
    private Long eventId;
    private boolean passed;
    private int score;

    public StudentTestResult toStudentTestresult() {
        return new StudentTestResult(
                this.studentId,
                this.eventId,
                this.passed,
                this.score);
    }
}
