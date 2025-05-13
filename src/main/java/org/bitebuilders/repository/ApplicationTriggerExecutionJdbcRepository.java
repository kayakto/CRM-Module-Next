package org.bitebuilders.repository;

import org.bitebuilders.model.ApplicationTriggerExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationTriggerExecutionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApplicationTriggerExecutionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void markExecuted(Long applicationId, Long statusId, Long triggerId) {
        jdbcTemplate.update("""
            INSERT INTO application_trigger_executions(application_id, status_id, trigger_id, executed, executed_at)
            VALUES (?, ?, ?, TRUE, now())
            ON CONFLICT (application_id, status_id, trigger_id) DO UPDATE
            SET executed = TRUE, executed_at = now()
        """, applicationId, statusId, triggerId);
    }

    public boolean isExecuted(Long applicationId, Long statusId, Long triggerId) {
        Boolean result = jdbcTemplate.queryForObject("""
            SELECT executed FROM application_trigger_executions
            WHERE application_id = ? AND status_id = ? AND trigger_id = ?
        """, Boolean.class, applicationId, statusId, triggerId);
        return Boolean.TRUE.equals(result);
    }

    public Optional<ApplicationTriggerExecution> find(Long applicationId, Long statusId, Long triggerId) {
        List<ApplicationTriggerExecution> result = jdbcTemplate.query("""
        SELECT application_id, status_id, trigger_id, executed, executed_at
        FROM application_trigger_executions
        WHERE application_id = ? AND status_id = ? AND trigger_id = ?
    """, (rs, rowNum) -> {
            ApplicationTriggerExecution execution = new ApplicationTriggerExecution();
            execution.setApplicationId(rs.getLong("application_id"));
            execution.setStatusId(rs.getLong("status_id"));
            execution.setTriggerId(rs.getLong("trigger_id"));
            execution.setExecuted(rs.getBoolean("executed"));
            execution.setExecutedAt(rs.getObject("executed_at", OffsetDateTime.class));
            return execution;
        }, applicationId, statusId, triggerId);

        return result.stream().findFirst();
    }
}
