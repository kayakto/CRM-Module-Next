package org.bitebuilders.telegram.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.telegram.model.StatusTrigger;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class StatusTriggerRepository {
    private static final Logger logger = LoggerFactory.getLogger(StatusTriggerRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatusTriggerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StatusTrigger> rowMapper = (rs, rowNum) -> {
        StatusTrigger trigger = new StatusTrigger();
        trigger.setStatusId(rs.getLong("status_id"));
        trigger.setTriggerId(rs.getLong("trigger_id"));
        trigger.setTriggerType(rs.getString("trigger_type"));

        // Handle status_triggers.parameters
        Object parametersObj = rs.getObject("parameters");
        if (parametersObj instanceof PGobject pgObject) {
            try {
                String json = pgObject.getValue();
                if (json != null) {
                    Map<String, Object> params = objectMapper.readValue(
                            json, new TypeReference<Map<String, Object>>() {});
                    trigger.setParameters(params);
                } else {
                    trigger.setParameters(Collections.emptyMap());
                }
            } catch (Exception e) {
                logger.error("Failed to parse parameters for trigger {}: {}",
                        rs.getLong("trigger_id"), e.getMessage());
                trigger.setParameters(Collections.emptyMap());
            }
        } else {
            trigger.setParameters(Collections.emptyMap());
        }

        // Handle triggers.parameters (trigger_parameters)
        Object triggerParametersObj = rs.getObject("trigger_parameters");
        if (triggerParametersObj instanceof PGobject pgObject) {
            try {
                String json = pgObject.getValue();
                if (json != null) {
                    Map<String, Object> params = objectMapper.readValue(
                            json, new TypeReference<Map<String, Object>>() {});
                    trigger.setTriggerParameters(params);
                } else {
                    trigger.setTriggerParameters(Collections.emptyMap());
                }
            } catch (Exception e) {
                logger.error("Failed to parse trigger_parameters for trigger {}: {}",
                        rs.getLong("trigger_id"), e.getMessage());
                trigger.setTriggerParameters(Collections.emptyMap());
            }
        } else {
            trigger.setTriggerParameters(Collections.emptyMap());
        }

        return trigger;
    };

    public List<StatusTrigger> findAllWithTriggerDetailsByStatus(Long statusId) {
        try {
            String sql = """
                SELECT st.status_id, st.trigger_id, st.parameters,
                       t.type as trigger_type, t.parameters as trigger_parameters
                FROM status_triggers st
                JOIN triggers t ON t.id = st.trigger_id
                WHERE st.status_id = ?
            """;
            return jdbcTemplate.query(sql, new Object[]{statusId}, rowMapper);
        } catch (Exception e) {
            logger.error("Failed to fetch triggers for status {}: {}", statusId, e.getMessage(), e);
            return List.of();
        }
    }

    public boolean isTriggerExecuted(Long applicationId, Long statusId, Long triggerId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*) 
                    FROM application_trigger_executions 
                    WHERE application_id = ? AND status_id = ? AND trigger_id = ?
                    """,
                    new Object[]{applicationId, statusId, triggerId}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.warn("Failed to check if trigger {} is executed for application {} and status {}: {}",
                    triggerId, applicationId, statusId, e.getMessage());
            return false;
        }
    }

    public void markTriggerExecuted(Long applicationId, Long statusId, Long triggerId) {
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO application_trigger_executions 
                    (application_id, status_id, trigger_id, executed_at)
                    VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                    ON CONFLICT DO NOTHING
                    """,
                    applicationId, statusId, triggerId);
            logger.debug("Marked trigger {} executed for application {} and status {}",
                    triggerId, applicationId, statusId);
        } catch (Exception e) {
            logger.error("Failed to mark trigger {} executed for application {} and status {}: {}",
                    triggerId, applicationId, statusId, e.getMessage());
        }
    }
}