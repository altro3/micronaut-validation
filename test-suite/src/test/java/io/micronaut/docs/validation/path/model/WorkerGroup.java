package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.docs.validation.path.validations.WorkerGroupValidation;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Introspected
@WorkerGroupValidation
public class WorkerGroup {
    @Pattern(regexp="[a-zA-Z0-9_-]+")
    private String key;
}
