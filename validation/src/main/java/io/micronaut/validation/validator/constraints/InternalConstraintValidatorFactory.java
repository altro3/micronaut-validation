/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.validation.validator.constraints;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.ConstraintTarget;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;

/**
 * Internal version of {@link ConstraintValidatorFactory}.
 *
 * @author Denis Stepanov
 * @since 4.3.0
 */
@Internal
public interface InternalConstraintValidatorFactory extends ConstraintValidatorFactory {

    /**
     * Find an instance {@link io.micronaut.validation.validator.constraints.ConstraintValidator}.
     *
     * @param validatorType    The validator type
     * @param targetType       The target type
     * @param constraintTarget The constraint target
     * @param <T>              The validator type
     * @return a new instance or null if not supported
     */
    @Nullable <T extends ConstraintValidator<?, ?>> T getInstance(@NonNull Class<T> validatorType,
                                                                  @NonNull Class<?> targetType,
                                                                  @NonNull ConstraintTarget constraintTarget);

}