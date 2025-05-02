package org.bitebuilders.repository;

import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.ApplicationStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationStatusJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ApplicationStatus> rowMapper = (rs, rowNum) -> {
        ApplicationStatus status = new ApplicationStatus();
        status.setId(rs.getLong("id"));
        status.setName(rs.getString("name"));
        status.setEventId(rs.getObject("event_id", Long.class)); // может быть null
        status.setIsSystem(rs.getBoolean("is_system"));
        status.setDisplayOrder(rs.getInt("display_order"));
        status.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return status;
    };

    public ApplicationStatusJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByIsSystemAndDisplayOrder(boolean isSystem, Integer displayOrder) {
        String sql = "SELECT COUNT(*) > 0 FROM application_statuses WHERE is_system = ? AND display_order = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, isSystem, displayOrder));
    }

    public boolean existsByEventIdAndDisplayOrder(Long eventId, Integer displayOrder) {
        String sql = "SELECT COUNT(*) > 0 FROM application_statuses WHERE event_id = ? AND display_order = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, eventId, displayOrder));
    }

    public List<ApplicationStatus> findByEventId(Long eventId) {
        String sql = "SELECT * FROM application_statuses WHERE event_id = ? ORDER BY display_order";
        return jdbcTemplate.query(sql, rowMapper, eventId);
    }

    public Optional<ApplicationStatus> findById(Long id) {
        String sql = "SELECT * FROM application_statuses WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) > 0 FROM application_statuses WHERE id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    public ApplicationStatus save(ApplicationStatus status) {
        return status.getId() == null ? insert(status) : update(status);
    }

    public List<ApplicationStatus> saveAll(List<ApplicationStatus> statuses) {
        return statuses.stream()
                .map(this::save)
                .toList();
    }

    private ApplicationStatus insert(ApplicationStatus status) {
        String sql = """
            INSERT INTO application_statuses 
            (name, event_id, is_system, display_order, updated_at) 
            VALUES (?, ?, ?, ?, ?) 
            RETURNING id, name, event_id, is_system, display_order, updated_at
            """;

        return jdbcTemplate.queryForObject(sql, rowMapper,
                status.getName(),
                status.getEventId(),
                status.getIsSystem(),
                status.getDisplayOrder(),
                status.getUpdatedAt());
    }

    private ApplicationStatus update(ApplicationStatus status) {
        String sql = """
            UPDATE application_statuses 
            SET name = ?, event_id = ?, is_system = ?, display_order = ?, updated_at = ? 
            WHERE id = ? 
            RETURNING id, name, event_id, is_system, display_order, updated_at
            """;

        return jdbcTemplate.queryForObject(sql, rowMapper,
                status.getName(),
                status.getEventId(),
                status.getIsSystem(),
                status.getDisplayOrder(),
                status.getUpdatedAt(),
                status.getId());
    }

    public void delete(ApplicationStatus status) {
        String sql = "DELETE FROM application_statuses WHERE id = ?";
        jdbcTemplate.update(sql, status.getId());
    }

    public boolean existsByStatusId(Long statusId) {
        String sql = "SELECT COUNT(*) > 0 FROM applications WHERE status_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, statusId));
    }

    public List<ApplicationStatus> findGlobalStatuses() {
        try {
            String sql = "SELECT * FROM application_statuses WHERE is_system = true AND event_id IS NULL ORDER BY display_order";
            return jdbcTemplate.query(sql, rowMapper);
        } catch (DataAccessException e) {
            throw new CustomNotFoundException("Failed to retrieve application statuses");
        }
    }
}
