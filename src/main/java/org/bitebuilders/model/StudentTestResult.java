package org.bitebuilders.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("student_test_results")
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class StudentTestResult {
    @Id
    private Long id;
    @Column("student_id")
    private Long studentId;
    @Column("event_id")
    private Long eventId;
    @Column("passed")
    private boolean passed;
    @Column("score")
    private int score;

    public StudentTestResult(Long studentId, Long eventId, boolean passed, int score) {
        this.studentId = studentId;
        this.eventId = eventId;
        this.passed = passed;
        this.score = score;
    }
}
