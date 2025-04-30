package org.bitebuilders.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.StandardField;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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
        try {
            String sql = """
                SELECT id, name, type, is_required, display_order, options
                FROM standard_fields
                ORDER BY display_order
                """;

            return jdbcTemplate.query(sql, this::mapRow);
        } catch (DataAccessException e) {
            System.out.println("Database error while fetching standard fields" + e);
            throw new CustomNotFoundException("Could not retrieve standard fields" + e);
        }
    }

    private StandardField mapRow(ResultSet rs, int rowNum) throws SQLException {
        StandardField field = new StandardField();
        field.setId(rs.getLong("id"));
        field.setName(rs.getString("name"));
        field.setType(rs.getString("type"));
        field.setRequired(rs.getBoolean("is_required"));
        field.setDisplayOrder(rs.getInt("display_order"));

        try {
            String optionsJson = rs.getString("options");
            field.setOptions(optionsJson != null ?
                    objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {}) :
                    Collections.emptyList());
        } catch (IOException e) {
            System.out.println("Failed to parse options JSON for field {}"+ rs.getLong("id")+ e);
            field.setOptions(Collections.emptyList());
        }

        return field;
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
