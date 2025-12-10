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
package dev.nonentity.validationspectester;

import static org.assertj.core.api.Assertions.*;

import dev.nonentity.validationspectester.fixtures.Contact;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Given I check that a bean instance field does not have an specific violation")
class FieldHasNoneOfErrorsTest {
  static Stream<Arguments> argumentValidationScenarios() {
    return Stream.of(
        // -- Field name is null
        Arguments.of(
            "When field name is null",
            null,
            new String[] {"must not be null"},
            "No field name provided to assert."),
        // -- Field name is blank
        Arguments.of(
            "When field name is blank",
            "",
            new String[] {"must not be null"},
            "No field name provided to assert."),
        // -- Unexpected violations is null
        Arguments.of(
            "When unexpected violations is null",
            "address",
            null,
            "No violation provided to assert."),
        // -- Unexpected violations is empty
        Arguments.of(
            "When unexpected violations is empty",
            "address",
            new String[] {},
            "No violation provided to assert."));
  }

  @ParameterizedTest
  @MethodSource("argumentValidationScenarios")
  void assertArgumentValidation(
      String scenario, String fieldName, String[] violations, String expectedMessage) {
    assertThatThrownBy(
            () -> StandardAssertConstraintViolation.givenBeanInstance(new Contact()).fieldHasNoneOfErrors(fieldName, violations))
        .withFailMessage(scenario)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(expectedMessage);
  }

  @Test
  @DisplayName("When the field has a violation exactly matching one of the unexpected violations")
  void hasMatchingViolation() {
    assertThatThrownBy(
            () ->
                StandardAssertConstraintViolation.givenBeanInstance(new Contact()).fieldHasNoneOfErrors("firstName", "must not be null"))
        .isInstanceOf(AssertionError.class)
        .hasMessage(
            "Field firstName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
  }

  @Test
  @DisplayName(
      "When the field does not have a violation exactly matching one of the unexpected violations")
  void doesNotHaveMatchingViolation() {
    AssertConstraintViolation testHelper = StandardAssertConstraintViolation.givenBeanInstance(new Contact());
    assertThat(testHelper.fieldHasNoneOfErrors("firstName", "length must be between 3 and 140"))
        .isSameAs(testHelper);
  }

  @Test
  @DisplayName(
      "When a validation group is received to assert that there are no expectation violations")
  void applyingValidationGroupsWhenReceived() {
    assertThatCode(
            () ->
                StandardAssertConstraintViolation.givenBeanInstance(new Contact())
                    .fieldHasNoneOfErrors("companyName", "must not be null"))
        .doesNotThrowAnyException();

    assertThatThrownBy(
            () ->
                StandardAssertConstraintViolation.givenBeanInstance(new Contact(), Contact.ProfessionalContact.class)
                    .fieldHasNoneOfErrors("companyName", "must not be null"))
        .isInstanceOf(AssertionError.class)
        .hasMessage(
            "Field companyName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
  }
}
