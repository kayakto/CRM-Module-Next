package org.bitebuilders.repository;

import lombok.RequiredArgsConstructor;
import org.bitebuilders.model.FormSystemField;
import org.bitebuilders.model.SystemField;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FormSystemFieldJdbcDao {
    private final JdbcTemplate jdbcTemplate;

    public void insert(FormSystemField formSystemField) {
        jdbcTemplate.update("""
            INSERT INTO form_system_fields 
            (form_id, system_field_id, is_required, display_order)
            VALUES (?, ?, ?, ?)
            """,
                formSystemField.getFormId(),
                formSystemField.getSystemFieldId(),
                formSystemField.getIsRequired(),
                formSystemField.getDisplayOrder()
        );
    }

    public List<SystemField> findByFormId(Long formId) {
        return jdbcTemplate.query("""
            SELECT sf.* 
            FROM system_fields sf
            JOIN form_system_fields fsf ON sf.id = fsf.system_field_id
            WHERE fsf.form_id = ?
            ORDER BY fsf.display_order
            """,
                new SystemFieldJdbcDao.SystemFieldRowMapper(),
                formId
        );
    }

    public Boolean isSystemFieldRequired(Long formId, Long systemFieldId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT is_required FROM form_system_fields " +
                            "WHERE form_id = ? AND system_field_id = ?",
                    Boolean.class,
                    formId, systemFieldId
            );
        } catch (EmptyResultDataAccessException e) {
            return true; // Системные поля по умолчанию обязательные
        }
    }

    public void deleteByFormId(Long formId) {
        jdbcTemplate.update("DELETE FROM form_system_fields WHERE form_id = ?", formId);
    }

    public void deleteByFormIdAndSystemFieldId(Long formId, Long systemFieldId) {
        jdbcTemplate.update("""
            DELETE FROM form_system_fields 
            WHERE form_id = ? AND system_field_id = ?
            """,
                formId, systemFieldId
        );
    }

    public Map<Long, Integer> findDisplayOrdersForForm(Long formId) {
        return jdbcTemplate.query(
                "SELECT system_field_id, display_order FROM form_system_fields WHERE form_id = ?",
                rs -> {
                    Map<Long, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getLong("system_field_id"), rs.getInt("display_order"));
                    }
                    return result;
                },
                formId
        );
    }

    public List<Long> findFormIdsBySystemField(Long systemFieldId) {
        return jdbcTemplate.queryForList(
                "SELECT form_id FROM form_system_fields WHERE system_field_id = ?",
                Long.class,
                systemFieldId
        );
    }
}
