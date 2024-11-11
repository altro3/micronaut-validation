package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.docs.validation.path.validations.FlowValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Introspected
@ToString
@EqualsAndHashCode
@FlowValidation
public class Flow implements DeletedInterface {

    @NotNull
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._-]+")
    String id;

    @NotNull
    @Pattern(regexp = "[a-z0-9._-]+")
    String namespace;

    @With
    @Min(value = 1)
    Integer revision;

    String description;

    List<Label> labels;

    Map<String, Object> variables;

    @Valid
    @NotEmpty
    List<Task> tasks;

    @Valid
    List<Task> errors;

    @Valid
    List<Listener> listeners;

    @Valid
    List<AbstractTrigger> triggers;

    List<TaskDefault> taskDefaults;

    @NotNull
    @Builder.Default
    boolean disabled = false;

    @NotNull
    @Builder.Default
    boolean deleted = false;

    public Logger logger() {
        return LoggerFactory.getLogger("flow." + this.id);
    }

    public static String uid(String namespace, String id, Optional<Integer> revision) {
        return String.join("_", Arrays.asList(
            namespace,
            id,
            String.valueOf(revision.orElse(-1))
        ));
    }

    public static String uidWithoutRevision(String namespace, String id) {
        return String.join("_", Arrays.asList(
            namespace,
            id
        ));
    }

    public Stream<String> allTypes() {
        return Stream.of(
                Optional.ofNullable(triggers).orElse(Collections.emptyList()).stream().map(AbstractTrigger::getType),
                allTasks().map(Task::getType),
                Optional.ofNullable(taskDefaults).orElse(Collections.emptyList()).stream().map(TaskDefault::getType)
            ).reduce(Stream::concat).orElse(Stream.empty())
            .distinct();
    }

    public Stream<Task> allTasks() {
        return Stream.of(
                this.tasks != null ? this.tasks : new ArrayList<Task>(),
                this.errors != null ? this.errors : new ArrayList<Task>(),
                this.listenersTasks()
            )
            .flatMap(Collection::stream);
    }

    public List<Task> allTasksWithChilds() {
        return allTasks()
            .flatMap(this::allTasksWithChilds)
            .collect(Collectors.toList());
    }

    private Stream<Task> allTasksWithChilds(Task task) {
        if (task == null) {
            return Stream.empty();
        } else if (task.isFlowable()) {
            Stream<Task> taskStream = ((FlowableTask<?>) task).allChildTasks()
                .stream()
                .flatMap(this::allTasksWithChilds);

            return Stream.concat(
                Stream.of(task),
                taskStream
            );
        } else {
            return Stream.of(task);
        }
    }

    public List<String> allTriggerIds() {
        return this.triggers != null ? this.triggers.stream()
            .map(AbstractTrigger::getId)
            .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<String> allTasksWithChildsAndTriggerIds() {
        return Stream.concat(
            this.allTasksWithChilds().stream()
                .map(Task::getId),
            this.allTriggerIds().stream()
        )
            .collect(Collectors.toList());
    }

    public List<Task> allErrorsWithChilds() {
        var allErrors = allTasksWithChilds().stream()
            .filter(task -> task.isFlowable() && ((FlowableTask<?>) task).getErrors() != null)
            .flatMap(task -> ((FlowableTask<?>) task).getErrors().stream())
            .collect(Collectors.toCollection(ArrayList::new));

        if (this.getErrors() != null && !this.getErrors().isEmpty()) {
            allErrors.addAll(this.getErrors());
        }

        return allErrors;
    }


    private List<Task> listenersTasks() {
        if (this.getListeners() == null) {
            return new ArrayList<>();
        }

        return this.getListeners()
            .stream()
            .flatMap(listener -> listener.getTasks().stream())
            .collect(Collectors.toList());
    }

    public Flow toDeleted() {
        return new Flow(
            this.id,
            this.namespace,
            this.revision + 1,
            this.description,
            this.labels,
            this.variables,
            this.tasks,
            this.errors,
            this.listeners,
            this.triggers,
            this.taskDefaults,
            this.disabled,
            true
        );
    }
}
