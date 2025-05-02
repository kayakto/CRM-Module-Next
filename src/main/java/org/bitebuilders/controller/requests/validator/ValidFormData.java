package org.bitebuilders.controller.requests.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FormDataValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFormData {
    String message() default "Invalid form data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
