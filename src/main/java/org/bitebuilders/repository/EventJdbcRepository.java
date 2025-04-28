package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) > 0 FROM events WHERE id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }
}
