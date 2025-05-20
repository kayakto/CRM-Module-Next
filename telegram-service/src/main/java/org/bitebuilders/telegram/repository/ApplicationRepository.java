package org.bitebuilders.telegram.repository;

import org.bitebuilders.telegram.model.Application;
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
        app.setCreatedAt(OffsetDateTime.ofInstant(rs.getTimestamp("created_at").toInstant(), ZoneOffset.UTC));
        app.setUpdatedAt(OffsetDateTime.ofInstant(rs.getTimestamp("updated_at").toInstant(), ZoneOffset.UTC));
        return app;
    };

    public ApplicationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Application> findById(Long id) {
        return jdbcTemplate.query("""
        SELECT * FROM applications WHERE id = ?
    """, new Object[]{id}, rs -> {
            if (rs.next()) return Optional.of(rowMapper.mapRow(rs, 1));
            else return Optional.empty();
        });
    }

    public Optional<Application> findByTelegramUrl(String telegramUrl) {
        return jdbcTemplate.query("""
            SELECT * FROM applications WHERE form_data ->> 'telegram_url' = ?
        """, new Object[]{telegramUrl}, rs -> {
            if (rs.next()) {
                return Optional.of(map(rs));
            } else {
                return Optional.empty();
            }
        });
    }

    public List<Application> findAllByTelegramUrl(String telegramUrl) {
        String sql = "SELECT * FROM applications WHERE form_data ->> 'telegram_url' = ?";
        return jdbcTemplate.query(sql, rowMapper, telegramUrl);
    }


    public Optional<Application> findByEmail(String email) {
        return jdbcTemplate.query("""
            SELECT * FROM applications WHERE form_data ->> 'email' = ?
        """, new Object[]{email}, rs -> {
            if (rs.next()) {
                return Optional.of(map(rs));
            } else {
                return Optional.empty();
            }
        });
    }

    public void updateTelegramUrl(Long applicationId, String newUrl) {
        jdbcTemplate.update("""
            UPDATE applications
            SET form_data = jsonb_set(form_data, '{telegram_url}', to_jsonb(?::text))
            WHERE id = ?
        """, newUrl, applicationId);
    }

    private Application map(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setId(rs.getLong("id"));
        app.setEventId(rs.getLong("event_id"));
        app.setFormData(rs.getString("form_data"));
        // парсить JSON можно через Jackson
        return app;
    }
}
