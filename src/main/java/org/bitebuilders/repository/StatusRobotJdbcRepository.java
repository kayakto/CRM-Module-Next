package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.StatusRobotWithRobot;
import org.bitebuilders.model.Robot;
import org.bitebuilders.model.StatusRobot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatusRobotJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RobotJdbcRepository robotRepository;

    public List<StatusRobot> findByStatusId(Long statusId) {
        String sql = "SELECT * FROM status_robots WHERE status_id = ? ORDER BY position ASC";
        return jdbcTemplate.query(sql, new Object[]{statusId}, statusRobotRowMapper());
    }

    public List<StatusRobotWithRobot> findRobotsByStatusId(Long statusId) {
        String sql = """
        SELECT sr.status_id, sr.robot_id, sr.position, sr.executed_at,
               r.id as r_id, r.name, r.type, r.parameters, r.created_at
        FROM status_robots sr
        JOIN robots r ON sr.robot_id = r.id
        WHERE sr.status_id = ?
        ORDER BY sr.position
    """;

        return jdbcTemplate.query(sql, new Object[]{statusId}, (rs, rowNum) -> {
            Robot robot = new Robot();
            robot.setId(rs.getLong("r_id"));
            robot.setName(rs.getString("name"));
            robot.setType(rs.getString("type"));
            robot.setParameters(robotRepository.readJson(rs.getString("parameters")));
            robot.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

            StatusRobotWithRobot sr = new StatusRobotWithRobot();
            sr.setStatusId(rs.getLong("status_id"));
            sr.setPosition(rs.getInt("position"));
            sr.setExecutedAt(rs.getObject("executed_at", OffsetDateTime.class));
            sr.setRobot(robot);

            return sr;
        });
    }

    public void save(StatusRobot statusRobot) {
        String sql = "INSERT INTO status_robots (status_id, robot_id, position) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, statusRobot.getStatusId(), statusRobot.getRobotId(), statusRobot.getPosition());
    }

    public void updatePosition(Long statusId, Long robotId, int position) {
        String sql = "UPDATE status_robots SET position = ? WHERE status_id = ? AND robot_id = ?";
        jdbcTemplate.update(sql, position, statusId, robotId);
    }

    public void delete(Long statusId, Long robotId) {
        String sql = "DELETE FROM status_robots WHERE status_id = ? AND robot_id = ?";
        jdbcTemplate.update(sql, statusId, robotId);
    }

    public int getNextPosition(Long statusId) {
        String sql = "SELECT COALESCE(MAX(position), 0) + 1 FROM status_robots WHERE status_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, statusId);
    }

    public Integer findPositionByRobotId(Long robotId) {
        String sql = "SELECT position FROM status_robots WHERE robot_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, robotId);
    }

    public Long findStatusIdByRobotId(Long robotId) {
        String sql = "SELECT status_id FROM status_robots WHERE robot_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, robotId);
    }

    private RowMapper<StatusRobot> statusRobotRowMapper() {
        return (rs, rowNum) -> {
            StatusRobot sr = new StatusRobot();
            sr.setStatusId(rs.getLong("status_id"));
            sr.setRobotId(rs.getLong("robot_id"));
            sr.setPosition(rs.getInt("position"));
            sr.setExecutedAt(rs.getObject("executed_at", OffsetDateTime.class));
            return sr;
        };
    }
}
