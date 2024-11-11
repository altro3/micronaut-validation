package io.micronaut.docs.validation.path.validations;

import io.micronaut.docs.validation.path.validations.validator.DagTaskValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DagTaskValidator.class)
public @interface DagTaskValidation {
    String message() default "invalid Dag task";
}
