package org.bitebuilders.controller.requests.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class FormDataValidator implements ConstraintValidator<ValidFormData, Map<String, Object>> {
    @Override
    public boolean isValid(Map<String, Object> value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty();
    }
}
