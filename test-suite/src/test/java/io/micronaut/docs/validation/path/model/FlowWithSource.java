package io.micronaut.docs.validation.path.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Introspected
@ToString
@Slf4j
public class FlowWithSource extends Flow {
    String source;

    public Flow toFlow() {
        return Flow.builder()
            .id(this.id)
            .namespace(this.namespace)
            .revision(this.revision)
            .description(this.description)
            .labels(this.labels)
            .variables(this.variables)
            .tasks(this.tasks)
            .errors(this.errors)
            .listeners(this.listeners)
            .triggers(this.triggers)
            .taskDefaults(this.taskDefaults)
            .disabled(this.disabled)
            .deleted(this.deleted)
            .build();
    }

    private static String cleanupSource(String source) {
        return source.replaceFirst("(?m)^revision: \\d+\n?","");
    }


    public static FlowWithSource of(Flow flow, String source) {
        return FlowWithSource.builder()
            .id(flow.id)
            .namespace(flow.namespace)
            .revision(flow.revision)
            .description(flow.description)
            .labels(flow.labels)
            .variables(flow.variables)
            .tasks(flow.tasks)
            .errors(flow.errors)
            .listeners(flow.listeners)
            .triggers(flow.triggers)
            .taskDefaults(flow.taskDefaults)
            .disabled(flow.disabled)
            .deleted(flow.deleted)
            .source(source)
            .build();
    }
}
