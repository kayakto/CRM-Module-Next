package org.bitebuilders.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.StandardField;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StandardFieldJdbcDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final RowMapper<StandardField> fieldRowMapper = (rs, rowNum) -> {
        StandardField field = new StandardField();
        field.setId(rs.getLong("id"));
        field.setName(rs.getString("name"));
        field.setType(rs.getString("type"));
        field.setRequired(rs.getBoolean("is_required"));
        field.setDisplayOrder(rs.getInt("display_order"));
        field.setOptions(convertJsonToOptions(rs.getString("options")));
        return field;
    };

    public List<StandardField> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM standard_fields ORDER BY display_order",
                fieldRowMapper
        );
    }

    public List<StandardField> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        String sql = String.format(
                "SELECT * FROM standard_fields WHERE id IN (%s) ORDER BY display_order",
                ids.stream().map(String::valueOf).collect(Collectors.joining(","))
        );

        return jdbcTemplate.query(sql, fieldRowMapper);
    }

    private List<String> convertJsonToOptions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse options JSON", e);
        }
    }
}
