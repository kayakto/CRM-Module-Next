package org.bitebuilders.telegram.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.telegram.model.Robot;
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
public class RobotRepository {
    private static final Logger logger = LoggerFactory.getLogger(RobotRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RobotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Robot> robotRowMapper = (rs, rowNum) -> {
        Robot robot = new Robot();
        robot.setId(rs.getLong("id"));
        robot.setType(rs.getString("type"));

        Object parametersObj = rs.getObject("parameters");
        if (parametersObj instanceof PGobject pgObject) {
            try {
                String json = pgObject.getValue();
                if (json != null) {
                    Map<String, Object> params = objectMapper.readValue(json, new TypeReference<>() {});
                    robot.setParameters(params);
                } else {
                    robot.setParameters(Collections.emptyMap());
                }
            } catch (Exception e) {
                logger.error("Failed to parse parameters for robot {}: {}", rs.getLong("id"), e.getMessage());
                robot.setParameters(Collections.emptyMap());
            }
        } else {
            robot.setParameters(Collections.emptyMap());
        }
        return robot;
    };

    public List<Robot> fetchRobotsForStatus(Long statusId) {
        try {
            String sql = """
                SELECT r.id, r.type, r.parameters
                FROM robots r
                JOIN status_robots sr ON r.id = sr.robot_id
                WHERE sr.status_id = ?
            """;
            return jdbcTemplate.query(sql, new Object[]{statusId}, robotRowMapper);
        } catch (Exception e) {
            logger.error("Failed to fetch robots for status {}: {}", statusId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean isRobotTypeExecuted(Long applicationId, Long statusId, String robotType) {
        try {
            String sql = """
                SELECT COUNT(*)
                FROM application_robot_executions are
                JOIN robots r ON r.id = are.robot_id
                WHERE are.application_id = ?
                AND are.status_id = ?
                AND r.type = ?
                AND are.executed = true
                AND are.callback_type = 'LINK_CLICK'
            """;
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{applicationId, statusId, robotType}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.warn("Failed to check if robot type {} is executed for application {} and status {}: {}",
                    robotType, applicationId, statusId, e.getMessage());
            return false;
        }
    }

    public boolean isRobotTypeSent(Long applicationId, Long statusId, String robotType) {
        try {
            String sql = """
                SELECT COUNT(*)
                FROM application_robot_executions are
                JOIN robots r ON r.id = are.robot_id
                WHERE are.application_id = ?
                AND are.status_id = ?
                AND r.type = ?
                AND are.executed = true
            """;
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{applicationId, statusId, robotType}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.warn("Failed to check if robot type {} is sent for application {} and status {}: {}",
                    robotType, applicationId, statusId, e.getMessage());
            return false;
        }
    }

    public boolean isCallbackReceived(Long applicationId, Long statusId, String callbackType) {
        try {
            String sql = """
            SELECT COUNT(*)
            FROM application_robot_executions
            WHERE application_id = ? AND status_id = ? AND callback_type = ?
        """;
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{applicationId, statusId, callbackType}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.warn("Failed to check if callback {} is received for application {} and status {}: {}", callbackType, applicationId, statusId, e.getMessage());
            return false;
        }
    }

    public void markRobotSent(Long applicationId, Long statusId, Long robotId) {
        try {
            String sql = """
            INSERT INTO application_robot_executions (application_id, status_id, robot_id, executed, executed_at)
            VALUES (?, ?, ?, true, CURRENT_TIMESTAMP)
            ON CONFLICT (application_id, status_id, robot_id) DO UPDATE
            SET executed = true, executed_at = CURRENT_TIMESTAMP
        """;
            int rowsAffected = jdbcTemplate.update(sql, applicationId, statusId, robotId);
            logger.info("Marked robot {} sent for application {} on status {}. Rows affected: {}", robotId, applicationId, statusId, rowsAffected);
        } catch (Exception e) {
            logger.error("Failed to mark robot {} sent for application {} on status {}: {}", robotId, applicationId, statusId, e.getMessage());
            throw new RuntimeException("Failed to mark robot sent", e);
        }
    }

    public void markRobotExecuted(Long applicationId, Long statusId, Long robotId, String callbackType) {
        try {
            String sql = """
            INSERT INTO application_robot_executions 
            (application_id, status_id, robot_id, executed, executed_at, callback_type, callback_received_at)
            VALUES (?, ?, ?, true, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (application_id, status_id, robot_id) 
            DO UPDATE SET 
                executed = true, 
                executed_at = CURRENT_TIMESTAMP,
                callback_type = COALESCE(EXCLUDED.callback_type, application_robot_executions.callback_type),
                callback_received_at = CASE 
                    WHEN EXCLUDED.callback_type IS NOT NULL THEN EXCLUDED.callback_received_at 
                    ELSE application_robot_executions.callback_received_at 
                END
        """;
            int rowsAffected = jdbcTemplate.update(sql, applicationId, statusId, robotId, callbackType);
            logger.info("Marked robot {} executed for application {} on status {} with callback {}. Rows affected: {}", robotId, applicationId, statusId, callbackType, rowsAffected);
        } catch (Exception e) {
            logger.error("Failed to mark robot {} executed for application {} on status {}: {}", robotId, applicationId, statusId, e.getMessage());
            throw new RuntimeException("Failed to mark robot executed", e);
        }
    }

    public void markCallbackReceived(Long applicationId, Long statusId, Long robotId, String callbackType) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE application_robot_executions " +
                            "SET callback_received_at = NOW(), callback_type = ? " +
                            "WHERE application_id = ? AND status_id = ? AND robot_id = ?",
                    callbackType, applicationId, statusId, robotId
            );
            if (rowsAffected > 0) {
                logger.info("Marked callback {} received for application {} on status {} for robot {}. Rows affected: {}", callbackType, applicationId, statusId, robotId, rowsAffected);
            } else {
                logger.warn("No rows updated for callback {} for application {} on status {} for robot {}. Check if the record exists.", callbackType, applicationId, statusId, robotId);
            }
        } catch (Exception e) {
            logger.error("Failed to mark callback {} for application {} on status {} for robot {}: {}", callbackType, applicationId, statusId, robotId, e.getMessage(), e);
            throw new RuntimeException("Failed to mark callback received", e);
        }
    }
}
