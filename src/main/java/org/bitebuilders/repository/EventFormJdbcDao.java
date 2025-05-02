package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.EventForm;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventFormJdbcDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EventForm> formRowMapper = (rs, rowNum) -> {
        EventForm form = new EventForm();
        form.setId(rs.getLong("id"));
        form.setEventId(rs.getLong("event_id"));
        form.setTitle(rs.getString("title"));
        form.setIsTemplate(rs.getBoolean("is_template"));
        form.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        form.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return form;
    };

    public Optional<EventForm> findByEventId(Long eventId) {
        try {
            EventForm form = jdbcTemplate.queryForObject(
                    "SELECT * FROM event_forms WHERE event_id = ?",
                    formRowMapper,
                    eventId
            );
            return Optional.ofNullable(form);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public EventForm save(EventForm form) {
        if (form.getId() == null) {
            Long id = jdbcTemplate.queryForObject(
                    "INSERT INTO event_forms (event_id, title, is_template, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, ?) RETURNING id",
                    Long.class,
                    form.getEventId(),
                    form.getTitle(),
                    form.getIsTemplate(),
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            form.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE event_forms SET title = ?, is_template = ?, updated_at = ? WHERE id = ?",
                    form.getTitle(),
                    form.getIsTemplate(),
                    OffsetDateTime.now(),
                    form.getId()
            );
        }
        return form;
    }

    public void deleteByEventId(Long eventId) {
        jdbcTemplate.update("DELETE FROM event_forms WHERE event_id = ?", eventId);
    }

    public List<EventForm> findAll() {
        return jdbcTemplate.query("SELECT * FROM event_forms", formRowMapper);
    }

    public boolean existsByEventId(Long eventId) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM event_forms WHERE event_id = ?)",
                Boolean.class,
                eventId
        );
        return exists != null && exists;
    }
}
