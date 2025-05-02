package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.Application;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean existsByStatusId(Long statusId) {
        String sql = "SELECT COUNT(*) > 0 FROM applications WHERE status_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, statusId));
    }

    public Application save(Application application) {
        if (application.getId() == null) {
            // INSERT
            String sql = "INSERT INTO applications (event_id, status_id, form_data, created_at, updated_at) " +
                    "VALUES (?, ?, ?::jsonb, ?, ?) RETURNING id";

            Long id = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{
                            application.getEventId(),
                            application.getStatusId(),
                            application.getFormData(),
                            application.getCreatedAt(),
                            application.getUpdatedAt()
                    },
                    Long.class
            );
            application.setId(id);
        } else {
            // UPDATE
            String sql = "UPDATE applications SET event_id = ?, status_id = ?, form_data = ?::jsonb, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(
                    sql,
                    application.getEventId(),
                    application.getStatusId(),
                    application.getFormData(),
                    application.getUpdatedAt(),
                    application.getId()
            );
        }

        return application;
    }

    public Optional<Application> findById(Long id) {
        String sql = "SELECT * FROM applications WHERE id = ?";
        List<Application> result = jdbcTemplate.query(sql, new Object[]{id}, applicationRowMapper());
        return result.stream().findFirst();
    }

    public List<Application> findAll() {
        String sql = "SELECT * FROM applications";
        return jdbcTemplate.query(sql, applicationRowMapper());
    }

    public List<Application> findByEventId(Long eventId) {
        String sql = "SELECT * FROM applications WHERE event_id = ?";
        return jdbcTemplate.query(sql, new Object[]{eventId}, applicationRowMapper());
    }

    public List<Application> findByStatusId(Long statusId) {
        String sql = "SELECT * FROM applications WHERE status_id = ?";
        return jdbcTemplate.query(sql, new Object[]{statusId}, applicationRowMapper());
    }

    public List<Application> findByEventIdAndStatusId(Long eventId, Long statusId) {
        String sql = "SELECT * FROM applications WHERE event_id = ? AND status_id = ?";
        return jdbcTemplate.query(sql, new Object[]{eventId, statusId}, applicationRowMapper());
    }

    public boolean existsByEventIdAndFormDataEmail(Long eventId, String email) {
        String sql = "SELECT COUNT(*) > 0 FROM applications " +
                "WHERE event_id = ? AND form_data ->> 'email' = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, eventId, email));
    }

    public Long findStatusByNameAndEventId(Long eventId, String name) {
        String sql = "SELECT id FROM application_statuses WHERE event_id = ? AND name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{eventId, name}, Long.class);
    }

    private RowMapper<Application> applicationRowMapper() {
        return (rs, rowNum) -> {
            Application app = new Application();
            app.setId(rs.getLong("id"));
            app.setEventId(rs.getLong("event_id"));
            app.setStatusId(rs.getLong("status_id"));
            app.setFormData(rs.getString("form_data")); // JSONB -> String
            app.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            app.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
            return app;
        };
    }
}
