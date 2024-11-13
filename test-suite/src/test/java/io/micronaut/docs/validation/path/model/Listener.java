package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@Introspected
public class Listener {
    String description;

    @Valid
    List<Condition> conditions;

    @Valid
    @NotEmpty
    List<Task> tasks;
}
