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

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Given I check that a bean instance field has expected violations")
class FieldHasErrorTest {

    static Stream<Arguments> argumentValidationScenarios() {
        return Stream.of(
                // -- Field name is null
                Arguments.of(
                        "When field name is null",
                        null,
                        new String[]{"must not be null."},
                        "No field name provided to assert."),
                // -- Field name is blank
                Arguments.of(
                        "When field name is blank",
                        "",
                        new String[]{"must not be null."},
                        "No field name provided to assert."),
                // -- Field name not found
                Arguments.of(
                        "When field name not found",
                        "address",
                        new String[]{"must not be null."},
                        "Field address not found or has no violations."),
                // -- Expected violations is null
                Arguments.of(
                        "When expected violations is null",
                        "firstName",
                        null,
                        "No violation provided to assert."),
                // -- Expected violations is empty
                Arguments.of(
                        "When expected violations is empty",
                        "firstName",
                        new String[]{},
                        "No violation provided to assert."));
    }

    @ParameterizedTest
    @MethodSource("argumentValidationScenarios")
    void assertArgumentValidation(
            String scenario, String fieldName, String[] violations, String expectedMessage) {
        assertThatThrownBy(() -> StandardAssertConstraintViolation.givenBeanInstance(new Contact()).fieldHasError(fieldName, violations))
                .withFailMessage(scenario)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("When the field has the expected violation")
    void withMatchingViolation() {
        AssertConstraintViolation testHelper = StandardAssertConstraintViolation.givenBeanInstance(new Contact());
        assertThat(testHelper.fieldHasError("firstName", "must not be null")).isSameAs(testHelper);
    }

    @Test
    @DisplayName("When the field does not have the expected violation")
    void withoutMatchingViolation() {
        assertThatThrownBy(
                () ->
                        StandardAssertConstraintViolation.givenBeanInstance(new Contact())
                                .fieldHasError("firstName", "length must be between 3 and 140"))
                .isInstanceOf(AssertionError.class)
                .hasMessage(
                        "Field firstName expected to have the violations [length must be between 3 and 140], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When the field has more violations than expected")
    void fieldHasMoreViolations() {
        Contact contact = new Contact();
        contact.setEmail("an");

        assertThatCode(
                () ->
                        StandardAssertConstraintViolation.givenBeanInstance(contact)
                                .fieldHasError("email", "must be a well-formed email address"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has less violations than expected")
    void fieldHasLessViolations() {
        assertThatThrownBy(
                () ->
                        StandardAssertConstraintViolation.givenBeanInstance(new Contact())
                                .fieldHasError(
                                        "firstName", "must not be null", "length must be between 3 and 140"))
                .isInstanceOf(AssertionError.class)
                .hasMessage(
                        "Field firstName expected to have the violations [length must be between 3 and 140, must not be null], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When validation groups are received")
    void appliedValidationGroupsWhenReceived() {
        assertThatThrownBy(
                () -> StandardAssertConstraintViolation.givenBeanInstance(new Contact()).fieldHasError("companyName", "must not be null"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Field companyName not found or has no violations.");

        assertThatCode(
                () ->
                        StandardAssertConstraintViolation.givenBeanInstance(new Contact(), Contact.ProfessionalContact.class)
                                .fieldHasError("companyName", "must not be null"))
                .doesNotThrowAnyException();
    }
}
