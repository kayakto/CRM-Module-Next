package org.bitebuilders.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.FormField;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FormFieldJdbcDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private final RowMapper<FormField> fieldRowMapper = (rs, rowNum) -> {
        FormField field = new FormField();
        field.setId(rs.getLong("id"));
        field.setFormId(rs.getLong("form_id"));
        field.setName(rs.getString("name"));
        field.setType(rs.getString("type"));
        field.setIsRequired(rs.getBoolean("is_required"));
        field.setDisplayOrder(rs.getInt("display_order"));
        field.setOptionsJson(rs.getString("options"));
        field.setOptions(convertJsonToOptions(rs.getString("options")));
        return field;
    };

    public void insertField(FormField field) {
        String optionsJson = convertOptionsToJson(field.getOptions());
        jdbcTemplate.update("""
            INSERT INTO form_fields 
            (form_id, name, type, is_required, display_order, options)
            VALUES (?, ?, ?, ?, ?, ?::jsonb)
            """,
                field.getFormId(),
                field.getName(),
                field.getType(),
                field.getIsRequired(),
                field.getDisplayOrder(),
                optionsJson
        );
    }

    public void deleteByFormId(Long formId) {
        jdbcTemplate.update("DELETE FROM form_fields WHERE form_id = ?", formId);
    }

    public List<FormField> findByFormIdOrderByDisplayOrder(Long formId) {
        return jdbcTemplate.query(
                "SELECT * FROM form_fields WHERE form_id = ? ORDER BY display_order",
                fieldRowMapper,
                formId
        );
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


