package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.util.Optional;

@SuperBuilder(toBuilder = true)
@Getter
@NoArgsConstructor
@Introspected
abstract public class Task {
    @NotNull
    @NotBlank
    @Pattern(regexp="[a-zA-Z0-9_-]+")
    protected String id;

    @NotNull
    @NotBlank
    @Pattern(regexp="\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*")
    protected String type;

    private String description;

    protected Duration timeout;

    @Builder.Default
    protected Boolean disabled = false;

    @Valid
    private WorkerGroup workerGroup;

    public Optional<Task> findById(String id) {
        if (this.getId().equals(id)) {
            return Optional.of(this);
        }

        if (this.isFlowable()) {
            Optional<Task> childs = ((FlowableTask<?>) this).allChildTasks()
                .stream()
                .map(t -> t.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

            if (childs.isPresent()) {
                return childs;
            }
        }

        return Optional.empty();
    }

    public boolean isFlowable() {
        return this instanceof FlowableTask;
    }

}
