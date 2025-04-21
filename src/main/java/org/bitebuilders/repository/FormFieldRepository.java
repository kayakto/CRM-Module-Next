package org.bitebuilders.repository;

import org.bitebuilders.model.FormField;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends CrudRepository<FormField, Long> {

    void deleteByFormId(Long formId);
    List<FormField> findByFormIdOrderByDisplayOrder(Long formId);
    void deleteByFormIdAndId(Long formId, Long fieldId);
}
