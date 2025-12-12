/*
 * The MIT License
 *
 *   Copyright (c) 2025, Andre Silva (contact.nonentity@tutamail.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package io.github.nonentitydev.validationspectester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract the usage of {@link Validator} by wrapping it in a helper class with a high level API to
 * assert violations on bean instance fields.
 */
public final class StandardAssertConstraintViolation implements AssertConstraintViolation {

    private final Map<String, Set<String>> violationsPerField;

    private StandardAssertConstraintViolation() {
        throw new UnsupportedOperationException(
                "This class was not meant to be instantiated using its default constructor.");
    }

    private StandardAssertConstraintViolation(Map<String, Set<String>> violationsPerField) {
        this.violationsPerField = violationsPerField;
    }

    /**
     * Creates a new instance of this class after validating the received bean instance, using the
     * received validator and optionally applying any validation group. The set of constraint
     * violation messages produced by the validation is grouped by property path and provided to this
     * class' constructor. The returned class instance has the single responsibility to assert against
     * the resulting map, not executing the validation again.
     *
     * @param instance         Bean instance being validated.
     * @param validator        Validator instance to be used to validate the received bean instance.
     * @param validationGroups Optional array of validation groups that will be used to validate the
     *                         received bean instance.
     * @return A new instance of this class populated with the constraints violation message grouped
     * by property path.
     */
    public static AssertConstraintViolation givenBeanInstance(
            Object instance, Validator validator, Class<?>... validationGroups) {
        Set<ConstraintViolation<Object>> violations = validator.validate(instance, validationGroups);
        Map<String, Set<String>> violationsPerField =
                violations.stream()
                        .collect(
                                Collectors.groupingBy(
                                        (ConstraintViolation<Object> violation) ->
                                                violation.getPropertyPath().toString(),
                                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));
        return new StandardAssertConstraintViolation(violationsPerField);
    }

    /**
     * Creates a new instance of this class after validating the received bean instance, using a default
     * validator initialized by this method and optionally applying any validation group. Apart from
     * initializing a default instance of {@link Validator}, this method delegates to {@link
     * #givenBeanInstance(Object, Validator, Class[])} method to produce a new instance of this class.
     *
     * @param instance         Bean instance being validated.
     * @param validationGroups Optional array of validation groups that will be used to validate the
     *                         received bean instance.
     * @return Instance of this class returned by {@link #givenBeanInstance(Object, Validator,
     * Class[])} "as-is".
     */
    public static AssertConstraintViolation givenBeanInstance(
            Object instance, Class<?>... validationGroups) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return givenBeanInstance(instance, validatorFactory.getValidator(), validationGroups);
        }
    }

    private StandardAssertConstraintViolation validateFieldNameArgumentProvided(String fieldName) {
        if ((fieldName == null) || (fieldName.isBlank())) {
            throw new IllegalArgumentException("No field name provided to assert.");
        }
        return this;
    }

    private StandardAssertConstraintViolation validateFieldNameArgumentIsPresent(String fieldName) {
        if (!this.violationsPerField.containsKey(fieldName)) {
            throw new IllegalArgumentException(
                    String.format("Field %s not found or has no violations.", fieldName));
        }
        return this;
    }

    private StandardAssertConstraintViolation validateViolationsArgument(
            String... expectedViolations) {
        if ((expectedViolations == null) || (expectedViolations.length == 0)) {
            throw new IllegalArgumentException("No violation provided to assert.");
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertConstraintViolation fieldHasError(String fieldName, String... expectedViolations) {
        this.validateFieldNameArgumentProvided(fieldName)
                .validateFieldNameArgumentIsPresent(fieldName)
                .validateViolationsArgument(expectedViolations);

        List<String> actual = this.violationsPerField.get(fieldName).stream().sorted().toList();
        List<String> expected = Stream.of(expectedViolations).sorted().toList();
        assertThat(actual)
                .withFailMessage(
                        "Field %s expected to have the violations %s, but violations %s were found.",
                        fieldName, expected, actual)
                .containsAll(expected);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertConstraintViolation fieldHasErrorContaining(
            String fieldName, String... expectedViolationsFragments) {
        this.validateFieldNameArgumentProvided(fieldName)
                .validateFieldNameArgumentIsPresent(fieldName)
                .validateViolationsArgument(expectedViolationsFragments);

        List<String> actual = this.violationsPerField.get(fieldName).stream().sorted().toList();
        List<String> expected = Stream.of(expectedViolationsFragments).sorted().toList();

        boolean allFragmentsMatching =
                expected.stream()
                        .allMatch(
                                (String expectedViolationFragment) -> {
                                    Predicate<String> containsSubString =
                                            (String actualViolation) ->
                                                    actualViolation.contains(expectedViolationFragment);

                                    Pattern regexPattern = Pattern.compile(expectedViolationFragment);
                                    Predicate<String> matchesRegexPattern =
                                            (String actualViolation) -> regexPattern.matcher(actualViolation).find();

                                    return actual.stream().anyMatch(containsSubString.or(matchesRegexPattern));
                                });

        assertThat(allFragmentsMatching)
                .withFailMessage(
                        "Field %s expected to have violations matching %s, but violations %s were found.",
                        fieldName, expected, actual)
                .isTrue();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertConstraintViolation fieldHasNoneOfErrors(
            String fieldName, String... unexpectedViolations) {
        this.validateFieldNameArgumentProvided(fieldName)
                .validateViolationsArgument(unexpectedViolations);

        List<String> actual =
                this.violationsPerField.getOrDefault(fieldName, new HashSet<>()).stream().sorted().toList();
        List<String> unexpected = Stream.of(unexpectedViolations).sorted().toList();

        assertThat(actual)
                .withFailMessage(
                        "Field %s not expected to have any of violations %s. Violations found for the field: %s",
                        fieldName, unexpected, actual)
                .doesNotContainAnyElementsOf(unexpected);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertConstraintViolation fieldHasNoneOfErrorsContaining(
            String fieldName, String... unexpectedViolations) {
        this.validateFieldNameArgumentProvided(fieldName)
                .validateViolationsArgument(unexpectedViolations);

        List<String> actual =
                this.violationsPerField.getOrDefault(fieldName, new HashSet<>()).stream().sorted().toList();
        List<String> unexpected = Stream.of(unexpectedViolations).sorted().toList();

        boolean allFragmentsMatching =
                unexpected.stream()
                        .anyMatch(
                                (String unexpectedViolationFragment) -> {
                                    Predicate<String> containsSubString =
                                            (String actualViolation) ->
                                                    actualViolation.contains(unexpectedViolationFragment);

                                    Pattern regexPattern = Pattern.compile(unexpectedViolationFragment);
                                    Predicate<String> matchesRegexPattern =
                                            (String actualViolation) -> regexPattern.matcher(actualViolation).find();

                                    return actual.stream().anyMatch(containsSubString.or(matchesRegexPattern));
                                });

        assertThat(allFragmentsMatching)
                .withFailMessage(
                        "Field %s not expected to have violations containing %s. Violations found for the field: %s",
                        fieldName, unexpected, actual)
                .isFalse();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertConstraintViolation fieldHasNoError(String fieldName) {
        this.validateFieldNameArgumentProvided(fieldName);
        assertThat(this.violationsPerField)
                .withFailMessage(
                        "Field %s not expected to have violations. Violations found: %s",
                        fieldName, this.violationsPerField.get(fieldName))
                .doesNotContainKey(fieldName);

        return this;
    }
}
