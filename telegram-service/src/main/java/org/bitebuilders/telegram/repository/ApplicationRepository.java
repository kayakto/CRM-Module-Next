package org.bitebuilders.telegram.repository;

import org.bitebuilders.telegram.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Application> rowMapper = (rs, rowNum) -> {
        Application app = new Application();
        app.setId(rs.getLong("id"));
        app.setEventId(rs.getLong("event_id"));
        app.setStatusId(rs.getLong("status_id"));
        app.setFormData(rs.getString("form_data"));
        app.setCreatedAt(OffsetDateTime.ofInstant(
                rs.getTimestamp("created_at").toInstant(), ZoneOffset.UTC));
        app.setUpdatedAt(OffsetDateTime.ofInstant(
                rs.getTimestamp("updated_at").toInstant(), ZoneOffset.UTC));
        return app;
    };

    public ApplicationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Application> findById(Long id) {
        try {
            return jdbcTemplate.query("""
                SELECT * FROM applications WHERE id = ?
            """, new Object[]{id}, rs -> {
                if (rs.next()) return Optional.of(rowMapper.mapRow(rs, 1));
                return Optional.empty();
            });
        } catch (Exception e) {
            logger.error("Failed to find application by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<Application> findByTelegramUrl(String telegramUrl) {
        try {
            return jdbcTemplate.query("""
                SELECT * FROM applications WHERE form_data ->> 'telegram_url' = ?
            """, new Object[]{telegramUrl}, rs -> {
                if (rs.next()) return Optional.of(rowMapper.mapRow(rs, 1));
                return Optional.empty();
            });
        } catch (Exception e) {
            logger.error("Failed to find application by telegram URL {}: {}",
                    telegramUrl, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<Application> findAllByTelegramUrl(String telegramUrl) {
        try {
            String sql = "SELECT * FROM applications WHERE form_data ->> 'telegram_url' = ?";
            return jdbcTemplate.query(sql, rowMapper, telegramUrl);
        } catch (Exception e) {
            logger.error("Failed to find applications by telegram URL {}: {}",
                    telegramUrl, e.getMessage(), e);
            return List.of();
        }
    }

    public Optional<Application> findByEmail(String email) {
        try {
            return jdbcTemplate.query("""
                SELECT * FROM applications WHERE form_data ->> 'email' = ?
            """, new Object[]{email}, rs -> {
                if (rs.next()) return Optional.of(rowMapper.mapRow(rs, 1));
                return Optional.empty();
            });
        } catch (Exception e) {
            logger.error("Failed to find application by email {}: {}", email, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public void updateTelegramUrl(Long applicationId, String newUrl) {
        try {
            jdbcTemplate.update("""
                UPDATE applications
                SET form_data = jsonb_set(form_data, '{telegram_url}', to_jsonb(?::text))
                WHERE id = ?
            """, newUrl, applicationId);
            logger.debug("Updated telegram URL for application {} to {}", applicationId, newUrl);
        } catch (Exception e) {
            logger.error("Failed to update telegram URL for application {}: {}",
                    applicationId, e.getMessage(), e);
            throw e;
        }
    }

    public void updateStatus(Long applicationId, Long newStatusId) {
        try {
            jdbcTemplate.update("""
                UPDATE applications
                SET status_id = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
            """, newStatusId, applicationId);
            logger.debug("Updated status for application {} to {}", applicationId, newStatusId);
        } catch (Exception e) {
            logger.error("Failed to update status for application {} to {}: {}",
                    applicationId, newStatusId, e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Application> findFirstByStatusId(Long statusId) {
        try {
            return jdbcTemplate.query("""
                SELECT * FROM applications
                WHERE status_id = ?
                ORDER BY updated_at ASC
                LIMIT 1
            """, new Object[]{statusId}, rs -> {
                if (rs.next()) return Optional.of(rowMapper.mapRow(rs, 1));
                return Optional.empty();
            });
        } catch (Exception e) {
            logger.error("Failed to find first application by status {}: {}",
                    statusId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<Application> findByStatusId(Long statusId) {
        try {
            String sql = "SELECT * FROM applications WHERE status_id = ?";
            return jdbcTemplate.query(sql, new Object[]{statusId}, rowMapper);
        } catch (Exception e) {
            logger.error("Failed to find applications by status {}: {}",
                    statusId, e.getMessage(), e);
            return List.of();
        }
    }

    public List<Long> findAllStatusIdsWithApplications() {
        try {
            String sql = "SELECT DISTINCT status_id FROM applications";
            return jdbcTemplate.queryForList(sql, Long.class);
        } catch (Exception e) {
            logger.error("Failed to find status IDs with applications: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRepository.class);
}
