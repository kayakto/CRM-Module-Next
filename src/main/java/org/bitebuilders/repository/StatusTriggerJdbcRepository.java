package org.bitebuilders.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.StatusTrigger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatusTriggerJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public void save(StatusTrigger statusTrigger) {
        String sql = """
            INSERT INTO status_triggers (status_id, trigger_id, parameters)
            VALUES (?, ?, ?::jsonb)
        """;
        jdbcTemplate.update(
                sql,
                statusTrigger.getStatusId(),
                statusTrigger.getTriggerId(),
                convertToJson(statusTrigger.getParameters())
        );
    }

    public void update(StatusTrigger statusTrigger) {
        String sql = """
            UPDATE status_triggers
            SET parameters = ?::jsonb
            WHERE status_id = ? AND trigger_id = ?
        """;
        jdbcTemplate.update(
                sql,
                convertToJson(statusTrigger.getParameters()),
                statusTrigger.getStatusId(),
                statusTrigger.getTriggerId()
        );
    }

    public void delete(Long statusId, Long triggerId) {
        String sql = "DELETE FROM status_triggers WHERE status_id = ? AND trigger_id = ?";
        jdbcTemplate.update(sql, statusId, triggerId);
    }

    public List<StatusTrigger> findByStatusId(Long statusId) {
        String sql = "SELECT * FROM status_triggers WHERE status_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StatusTrigger st = new StatusTrigger();
            st.setStatusId(rs.getLong("status_id"));
            st.setTriggerId(rs.getLong("trigger_id"));
            st.setParameters(readJson(rs.getString("parameters")));
            return st;
        }, statusId);
    }

    public boolean isLinkedToStatus(Long statusId, Long triggerId) {
        String sql = """
        SELECT EXISTS(
            SELECT 1 FROM status_triggers
            WHERE status_id = ? AND trigger_id = ?
        )
    """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, statusId, triggerId));
    }

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }

    private Map<String, Object> readJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка десериализации JSON", e);
        }
    }
}

