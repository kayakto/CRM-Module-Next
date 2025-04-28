package org.bitebuilders.service;

import org.bitebuilders.model.EventForm;
import org.bitebuilders.model.FormField;
import org.bitebuilders.model.StandardField;
import org.bitebuilders.repository.EventFormRepository;
import org.bitebuilders.repository.FormFieldJdbcDao;
import org.bitebuilders.repository.StandardFieldJdbcDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class EventFormService {
    private final EventFormRepository formRepository;
    private final FormFieldJdbcDao formFieldJdbcDao;
    private final StandardFieldJdbcDao standardFieldJdbcDao;

    public EventFormService(EventFormRepository formRepository,
                            FormFieldJdbcDao formFieldJdbcDao,
                            StandardFieldJdbcDao standardFieldJdbcDao) {
        this.formRepository = formRepository;
        this.formFieldJdbcDao = formFieldJdbcDao;
        this.standardFieldJdbcDao = standardFieldJdbcDao;
    }

    public EventForm createForm(Long eventId, String title, Boolean isTemplate, List<Long> selectedFieldIds) {
        EventForm form = new EventForm();
        form.setEventId(eventId);
        form.setTitle(title);
        form.setCreatedAt(OffsetDateTime.now());
        form.setUpdatedAt(OffsetDateTime.now());
        form.setIsTemplate(isTemplate != null ? isTemplate : false);

        EventForm savedForm = formRepository.save(form);

        // Сохраняем поля формы
        saveFormFields(savedForm.getId(), selectedFieldIds);

        // Загружаем форму с полями перед возвратом
        return getFormWithFields(savedForm.getId());
    }

    private void saveFormFields(Long formId, List<Long> selectedFieldIds) {
        List<StandardField> selectedFields = standardFieldJdbcDao.findByIds(selectedFieldIds);

        for (StandardField standardField : selectedFields) {
            FormField formField = new FormField();
            formField.setFormId(formId);
            formField.setName(standardField.getName());
            formField.setType(standardField.getType());
            formField.setIsRequired(standardField.isRequired());
            formField.setDisplayOrder(standardField.getDisplayOrder());
            formField.setOptions(standardField.getOptions());

            formFieldJdbcDao.insertField(formField);
        }
    }

    public EventForm updateForm(Long formId, String title, List<Long> selectedFieldIds, Boolean isTemplate) {
        EventForm form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        form.setTitle(title);
        form.setIsTemplate(isTemplate);
        form.setUpdatedAt(OffsetDateTime.now());

        // Удаляем текущие поля формы
        formFieldJdbcDao.deleteByFormId(formId);

        // Добавляем новые выбранные поля
        List<StandardField> selectedFields = standardFieldJdbcDao.findByIds(selectedFieldIds);
        for (StandardField standardField : selectedFields) {
            FormField formField = new FormField();
            formField.setFormId(formId);
            formField.setName(standardField.getName());
            formField.setType(standardField.getType());
            formField.setIsRequired(standardField.isRequired());
            formField.setDisplayOrder(standardField.getDisplayOrder());
            formField.setOptions(standardField.getOptions());

            formFieldJdbcDao.insertField(formField);
        }

        formRepository.save(form);

        // Загружаем форму с полями перед возвратом
        return getFormWithFields(formId);
    }

    public List<StandardField> getAllStandardFields() {
        return standardFieldJdbcDao.findAll();
    }

    // Остальные методы остаются без изменений
    public void deleteForm(Long formId) {
        formRepository.deleteById(formId);
    }

    @Transactional(readOnly = true)
    public List<EventForm> getAllForms(boolean withFields) {
        List<EventForm> forms = (List<EventForm>) formRepository.findAll();
        if (withFields) {
            forms.forEach(form ->
                    form.setFields(formFieldJdbcDao.findByFormIdOrderByDisplayOrder(form.getId())));
        }
        return forms;
    }

    public EventForm getFormWithFields(Long formId) {
        EventForm form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        List<FormField> fields = formFieldJdbcDao.findByFormIdOrderByDisplayOrder(formId);
        form.setFields(fields); // Убедитесь, что в EventForm есть поле fields

        return form;
    }
}
