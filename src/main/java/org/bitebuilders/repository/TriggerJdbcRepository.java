package org.bitebuilders.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.Trigger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TriggerJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public Trigger save(Trigger trigger) {
        String sql = """
            INSERT INTO triggers (name, type, parameters, created_at)
            VALUES (?, ?, ?::jsonb, ?)
            RETURNING id
        """;

        String json = convertToJson(trigger.getParameters());
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                trigger.getName(), trigger.getType(), json, trigger.getCreatedAt() != null ? trigger.getCreatedAt() : OffsetDateTime.now());

        trigger.setId(id);
        return trigger;
    }

    public void update(Trigger trigger) {
        String sql = "UPDATE triggers SET name = ?, type = ?, parameters = ?::jsonb WHERE id = ?";
        jdbcTemplate.update(sql, trigger.getName(), trigger.getType(), convertToJson(trigger.getParameters()), trigger.getId());
    }

    public Optional<Trigger> findById(Long id) {
        String sql = "SELECT * FROM triggers WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Trigger trigger = new Trigger();
                trigger.setId(rs.getLong("id"));
                trigger.setName(rs.getString("name"));
                trigger.setType(rs.getString("type"));
                try {
                    trigger.setParameters(objectMapper.readValue(rs.getString("parameters"), new TypeReference<>() {})); // или deserialize
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                trigger.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                return Optional.of(trigger);
            }
            return Optional.empty();
        }, id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM triggers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}

