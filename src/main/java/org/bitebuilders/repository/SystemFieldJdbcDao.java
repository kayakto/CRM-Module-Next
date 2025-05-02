package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.SystemField;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SystemFieldJdbcDao {
    private final JdbcTemplate jdbcTemplate;

    public static class SystemFieldRowMapper implements RowMapper<SystemField> {
        @Override
        public SystemField mapRow(ResultSet rs, int rowNum) throws SQLException {
            SystemField field = new SystemField();
            field.setId(rs.getLong("id"));
            field.setName(rs.getString("name"));
            field.setType(rs.getString("type"));
            field.setIsRequired(rs.getBoolean("is_required"));
            field.setDisplayOrder(rs.getInt("display_order"));
            return field;
        }
    }

    public List<SystemField> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM system_fields ORDER BY display_order",
                new SystemFieldRowMapper()
        );
    }

    public Optional<SystemField> findById(Long id) {
        try {
            SystemField field = jdbcTemplate.queryForObject(
                    "SELECT * FROM system_fields WHERE id = ?",
                    new SystemFieldRowMapper(),
                    id
            );
            return Optional.ofNullable(field);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<SystemField> findByFormId(Long formId) {
        return jdbcTemplate.query(
                "SELECT sf.* FROM system_fields sf " +
                        "JOIN form_system_fields fsf ON sf.id = fsf.system_field_id " +
                        "WHERE fsf.form_id = ? ORDER BY fsf.display_order",
                new SystemFieldRowMapper(),
                formId
        );
    }
}