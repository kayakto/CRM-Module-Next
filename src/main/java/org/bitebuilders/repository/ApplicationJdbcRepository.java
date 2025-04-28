package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean existsByStatusId(Long statusId) {
        String sql = "SELECT COUNT(*) > 0 FROM applications WHERE status_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, statusId));
    }
}