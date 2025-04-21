package org.bitebuilders.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitebuilders.model.FormField;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class FormFieldJdbcDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public FormFieldJdbcDao(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void insertField(FormField field) {
        jdbcTemplate.update("""
            INSERT INTO form_fields (form_id, name, type, is_required, display_order, options)
            VALUES (?, ?, ?, ?, ?, ?::jsonb)
        """,
                field.getFormId(),
                field.getName(),
                field.getType(),
                field.getIsRequired(),
                field.getDisplayOrder(),
                convertOptionsToJson(field.getOptions())
        );
    }

    public void deleteByFormId(Long formId) {
        jdbcTemplate.update("DELETE FROM form_fields WHERE form_id = ?", formId);
    }

    public void deleteByFormIdAndId(Long formId, Long fieldId) {
        jdbcTemplate.update("DELETE FROM form_fields WHERE form_id = ? AND id = ?", formId, fieldId);
    }

    public List<FormField> findByFormIdOrderByDisplayOrder(Long formId) {
        return jdbcTemplate.query("""
            SELECT id, form_id, name, type, is_required, display_order, options
            FROM form_fields
            WHERE form_id = ?
            ORDER BY display_order
        """, new Object[]{formId}, (rs, rowNum) -> {
            FormField field = new FormField();
            field.setId(rs.getLong("id"));
            field.setFormId(rs.getLong("form_id"));
            field.setName(rs.getString("name"));
            field.setType(rs.getString("type"));
            field.setIsRequired(rs.getBoolean("is_required"));
            field.setDisplayOrder(rs.getInt("display_order"));
            field.setOptions(convertJsonToOptions(rs.getString("options")));
            return field;
        });
    }

    private String convertOptionsToJson(List<String> options) {
        try {
            return objectMapper.writeValueAsString(options != null ? options : List.of());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize options", e);
        }
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


