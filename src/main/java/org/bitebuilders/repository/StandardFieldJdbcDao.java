package org.bitebuilders.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.model.StandardField;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StandardFieldJdbcDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public StandardFieldJdbcDao(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<StandardField> findAll() {
        return jdbcTemplate.query("""
            SELECT id, name, type, is_required, display_order, options
            FROM standard_fields
            ORDER BY display_order
        """, (rs, rowNum) -> {
            StandardField field = new StandardField();
            field.setId(rs.getLong("id"));
            field.setName(rs.getString("name"));
            field.setType(rs.getString("type"));
            field.setRequired(rs.getBoolean("is_required"));
            field.setDisplayOrder(rs.getInt("display_order"));
            field.setOptions(convertJsonToOptions(rs.getString("options")));
            return field;
        });
    }

    public List<StandardField> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        String sql = String.format("""
            SELECT id, name, type, is_required, display_order, options
            FROM standard_fields
            WHERE id IN (%s)
            ORDER BY display_order
        """, ids.stream().map(String::valueOf).collect(Collectors.joining(",")));

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StandardField field = new StandardField();
            field.setId(rs.getLong("id"));
            field.setName(rs.getString("name"));
            field.setType(rs.getString("type"));
            field.setRequired(rs.getBoolean("is_required"));
            field.setDisplayOrder(rs.getInt("display_order"));
            field.setOptions(convertJsonToOptions(rs.getString("options")));
            return field;
        });
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
