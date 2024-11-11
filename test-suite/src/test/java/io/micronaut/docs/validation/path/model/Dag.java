package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.docs.validation.path.validations.DagTaskValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@DagTaskValidation
public class Dag extends Task{
    @NotNull
    @Builder.Default
    private final Integer concurrent = 0;

    @NotEmpty
    @Valid
    private List<DagTask> dagTasks;

    @Valid
    protected List<Task> errors;

    public List<String> dagCheckNotExistTask(List<DagTask> taskDepends) {
        List<String> dependenciesIds = taskDepends
            .stream()
            .map(DagTask::getDependsOn)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .toList();

        List<String> tasksIds = taskDepends
            .stream()
            .map(taskDepend -> taskDepend.getTask().getId())
            .toList();

        return dependenciesIds.stream()
            .filter(dependencyId -> !tasksIds.contains(dependencyId))
            .collect(Collectors.toList());
    }

    public ArrayList<String> dagCheckCyclicDependencies(List<DagTask> taskDepends) {
        ArrayList<String> cyclicDependency = new ArrayList<>();
        taskDepends.forEach(taskDepend -> {
            if (taskDepend.getDependsOn() != null) {
                List<String> nestedDependencies = this.nestedDependencies(taskDepend, taskDepends, new ArrayList<>());
                if (nestedDependencies.contains(taskDepend.getTask().getId())) {
                    cyclicDependency.add(taskDepend.getTask().getId());
                }
            }
        });

        return cyclicDependency;
    }

    private ArrayList<String> nestedDependencies(DagTask taskDepend, List<DagTask> tasks, List<String> visited) {
        final ArrayList<String> localVisited = new ArrayList<>(visited);
        if (taskDepend.getDependsOn() != null) {
            taskDepend.getDependsOn()
                .stream()
                .filter(depend -> !localVisited.contains(depend))
                .forEach(depend -> {
                    localVisited.add(depend);
                    Optional<DagTask> task = tasks
                        .stream()
                        .filter(t -> t.getTask().getId().equals(depend))
                        .findFirst();

                    if (task.isPresent()) {
                        localVisited.addAll(this.nestedDependencies(task.get(), tasks, localVisited));
                    }
                });
        }
        return localVisited;
    }

    @SuperBuilder
    @ToString
    @EqualsAndHashCode
    @Getter
    @NoArgsConstructor
    @Introspected
    public static class DagTask {
        @NotNull
        private Task task;

        private List<String> dependsOn;
    }
}
