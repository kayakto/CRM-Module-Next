package org.bitebuilders.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.Robot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RobotJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public Robot save(Robot robot) {
        String sql = """
            INSERT INTO robots (name, type, parameters, created_at)
            VALUES (?, ?, ?::jsonb, ?)
            RETURNING id
        """;

        String json = convertToJson(robot.getParameters());
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                robot.getName(), robot.getType(), json, robot.getCreatedAt() != null ? robot.getCreatedAt() : OffsetDateTime.now());

        robot.setId(id);
        return robot;
    }

    public Optional<Robot> findById(Long id) {
        String sql = "SELECT * FROM robots WHERE id = ?";
        List<Robot> robots = jdbcTemplate.query(sql, robotRowMapper(), id);
        return robots.stream().findFirst();
    }

    public void update(Robot robot) {
        String sql = "UPDATE robots SET name = ?, type = ?, parameters = ?::jsonb WHERE id = ?";
        jdbcTemplate.update(sql, robot.getName(), robot.getType(), convertToJson(robot.getParameters()), robot.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM robots WHERE id = ?", id);
    }

    public List<Robot> findAllByStatusId(Long statusId) {
        String sql = """
            SELECT r.*
            FROM robots r
            JOIN status_robots sr ON r.id = sr.robot_id
            WHERE sr.status_id = ?
            ORDER BY sr.position
        """;

        return jdbcTemplate.query(sql, robotRowMapper(), statusId);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) > 0 FROM robots WHERE id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private RowMapper<Robot> robotRowMapper() {
        return (rs, rowNum) -> {
            Robot robot = new Robot();
            robot.setId(rs.getLong("id"));
            robot.setName(rs.getString("name"));
            robot.setType(rs.getString("type"));
            String json = rs.getString("parameters");
            robot.setParameters(readJson(json));
            robot.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return robot;
        };
    }

    Map<String, Object> readJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON parameters", e);
        }
    }

    private String convertToJson(Map<String, Object> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize parameters", e);
        }
    }
}


